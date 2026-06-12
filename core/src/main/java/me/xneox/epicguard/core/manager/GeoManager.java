/*
 * EpicGuardPlus is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * EpicGuardPlus is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package me.xneox.epicguard.core.manager;

import com.maxmind.db.CHMCache;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import me.xneox.epicguard.core.EpicGuard;
import me.xneox.epicguard.core.util.FileUtils;
import me.xneox.epicguard.core.util.LogUtils;
import me.xneox.epicguard.core.util.TextUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.jetbrains.annotations.NotNull;

/**
 * This class manages the GeoLite2's databases, downloads and updates them if needed. It also
 * contains methods for easy database access.
 */
public class GeoManager {
  private final EpicGuard epicGuard;

  private DatabaseReader countryReader;
  private DatabaseReader cityReader;

  public GeoManager(EpicGuard epicGuard) {
    this.epicGuard = epicGuard;
    epicGuard.logger().info("This product includes GeoLite2 data created by MaxMind, available from https://www.maxmind.com");

    var parent = new File(FileUtils.EPICGUARD_DIR, "data");
    //noinspection ResultOfMethodCallIgnored
    parent.mkdirs();

    var countryDatabasePath = epicGuard.config().misc().geoCountryDatabaseFile();
    var cityDatabasePath = epicGuard.config().misc().geoCityDatabaseFile();
    var customCountryDatabase = this.hasText(countryDatabasePath);
    var customCityDatabase = this.hasText(cityDatabasePath);

    var countryDatabase = this.databaseFile(
        countryDatabasePath,
        new File(parent, "GeoLite2-Country.mmdb"),
        "country");
    var cityDatabase = this.databaseFile(
        cityDatabasePath,
        new File(parent, "GeoLite2-City.mmdb"),
        "city");

    var countryArchive = new File(parent, "GeoLite2-Country.tar.gz");
    var cityArchive = new File(parent, "GeoLite2-City.tar.gz");

    // Check if geo database download is enabled
    if (!epicGuard.config().misc().geoDatabaseDownload()) {
      epicGuard.logger().info("GeoIP database download is disabled in configuration.");
      // Try to use existing databases if available
      this.loadDatabases(countryDatabase, cityDatabase);
      return;
    }

    var licenseKey = epicGuard.config().misc().maxmindLicenseKey();
    if (licenseKey == null || licenseKey.isEmpty()) {
      epicGuard.logger().warn("MaxMind license key is not configured! GeoIP features (country/city checks) will be disabled.");
      epicGuard.logger().warn("Get your free license key at: https://www.maxmind.com/en/geolite2/signup");
      epicGuard.logger().warn("Then set it in settings.conf under misc.maxmind-license-key");

      // Try to use existing databases if they were downloaded before
      this.loadDatabases(countryDatabase, cityDatabase);
      return;
    }

    try {
      if (!customCountryDatabase) {
        this.downloadDatabase(
            countryDatabase,
            countryArchive,
            "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-Country&license_key=" + licenseKey + "&suffix=tar.gz");
      }
      if (!customCityDatabase) {
        this.downloadDatabase(
            cityDatabase,
            cityArchive,
            "https://download.maxmind.com/app/geoip_download?edition_id=GeoLite2-City&license_key=" + licenseKey + "&suffix=tar.gz");
      }

      this.loadDatabases(countryDatabase, cityDatabase);
    } catch (IOException ex) {
      LogUtils.catchException("Couldn't download the GeoIP databases. Check your license key and internet connection.", ex);
    }
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }

  private File databaseFile(@NotNull String configuredPath, @NotNull File defaultFile, @NotNull String databaseName) {
    if (!this.hasText(configuredPath)) {
      return defaultFile;
    }

    var configuredFile = new File(configuredPath);
    if (!configuredFile.exists()) {
      this.epicGuard.logger().warn("Configured GeoIP " + databaseName + " database does not exist: " + configuredFile.getPath());
    }
    return configuredFile;
  }

  private void loadDatabases(@NotNull File countryDatabase, @NotNull File cityDatabase) {
    this.loadDatabase(countryDatabase, "country", reader -> this.countryReader = reader);
    this.loadDatabase(cityDatabase, "city", reader -> this.cityReader = reader);
  }

  private void loadDatabase(@NotNull File database, @NotNull String databaseName, @NotNull Consumer<DatabaseReader> readerConsumer) {
    if (!database.exists()) {
      this.epicGuard.logger().warn("GeoIP " + databaseName + " database was not found: " + database.getPath());
      return;
    }

    try {
      readerConsumer.accept(new DatabaseReader.Builder(database).withCache(new CHMCache()).build());
      this.epicGuard.logger().info("Loaded GeoIP " + databaseName + " database: " + database.getPath());
    } catch (IOException ex) {
      this.epicGuard.logger().warn("Couldn't load GeoIP " + databaseName + " database: " + ex.getMessage());
    }
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  private void downloadDatabase(@NotNull File database, @NotNull File archive, @NotNull String url) throws IOException {
    if (!database.exists() || System.currentTimeMillis() - database.lastModified() > TimeUnit.DAYS.toMillis(7L)) {
      // Database does not exist or is outdated, and need to be downloaded.
      this.epicGuard.logger().info("Downloading the GeoIP database file: " + database.getName());
      FileUtils.downloadFile(url, archive);

      this.epicGuard.logger().info("Extracting the database from the tar archive...");
      var tarInput = new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(archive)));

      var entry = tarInput.getNextTarEntry();
      while (entry != null) {
        // Extracting the database (.mmdb) database we are looking for.
        if (entry.getName().endsWith(database.getName())) {
          IOUtils.copy(tarInput, new FileOutputStream(database));
        }

        entry = tarInput.getNextTarEntry();
      }

      // Closing InputStream and removing archive file.
      tarInput.close();
      archive.delete();
      this.epicGuard.logger().info("Database (" + database.getName() + ") has been extracted succesfuly.");
    }
  }

  @NotNull
  public String countryCode(@NotNull String address) {
    var inetAddress = TextUtils.parseAddress(address);
    if (inetAddress != null && this.countryReader != null) {
      try {
        return this.valueOrUnknown(this.countryReader.country(inetAddress).getCountry().getIsoCode());
      } catch (IOException | GeoIp2Exception ex) {
        this.epicGuard.logger().warn("Couldn't find the country for the address " + address + ": " + ex.getMessage());
      }
    }
    return "unknown";
  }

  @NotNull
  public String city(@NotNull String address) {
    var inetAddress = TextUtils.parseAddress(address);
    if (inetAddress != null && this.cityReader != null) {
      try {
        return this.valueOrUnknown(this.cityReader.city(inetAddress).getCity().getName());
      } catch (IOException | GeoIp2Exception ex) {
        this.epicGuard.logger().warn("Couldn't find the city for the address " + address + ": " + ex.getMessage());
      }
    }
    return "unknown";
  }

  private String valueOrUnknown(String value) {
    return value == null || value.isBlank() ? "unknown" : value;
  }
}
