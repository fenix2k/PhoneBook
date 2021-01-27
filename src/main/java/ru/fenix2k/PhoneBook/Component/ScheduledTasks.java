package ru.fenix2k.PhoneBook.Component;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Service.ImportProvider;

import java.util.List;

/**
 * Scheduler service.
 * Runs jobs according to the schedule
 */
@Component
@Log4j
public class ScheduledTasks {

  /** Provider of import data */
  @Autowired
  private ImportProvider importProvider;

  /**
   * Runs an import employees from ldap server.
   * Scheduled by cron
   */
  @Scheduled(cron = "${phonebook.cron.job.import-from-ldap}")
  public void importEmployeesFromLdapServer() {
    log.info("Start job import from Ldap server");
    try {
      List<Employee> employeeList = importProvider.importFromExternalProvider();
      if (employeeList.size() > 0) {
        log.info("Import from Ldap server successful");
        log.debug("Import data: " + employeeList);
      }
      else
        log.info("Import from Ldap server successful. Nothing was imported.");
    }
    catch (Exception ex) {
      log.info("Error: could not connect to Ldap Server");
      log.debug("Getting an error: " + ex.getMessage());
    }
  }

}
