package ru.fenix2k.PhoneBook;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.fenix2k.PhoneBook.Entity.Employee;
import ru.fenix2k.PhoneBook.Service.EmployeeService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class PhoneBookApplicationTests {

	@Autowired
	private EmployeeService employeeService;

	private List<Employee> employeeList;

//	@BeforeAll
//	void init() {
//	}
//
//	@AfterAll
//	void end() {
//	}

	//@Test
	void createTestEmployees() {
		this.employeeList = new ArrayList<>(List.of(
				new Employee("lastname1 firstname1 middlename1", "email1@mail.ru","title1","department1","company1","1111","8(495)1111111","8(916)1111111","address1"),
				new Employee("lastname2 firstname2 middlename2", "email2@mail.ru","title1","department2","company2","2222","8(495)2222222","8(916)2222222","address2"),
				new Employee("lastname3 firstname3 middlename3", "email3@mail.ru","title3","department3","company3","3333","8(495)3333333","8(916)3333333","address3"),
				new Employee("lastname4 firstname4 middlename4", "email4@mail.ru","title4","department4","company4","4444","8(495)4444444","8(916)4444444","address4"),
				new Employee("lastname5 firstname5 middlename5", "email6@mail.ru","title5","department5","company5","5555","8(495)5555555","8(916)5555555","address5"),
				new Employee("lastname6 firstname6 middlename6", "email7@mail.ru","title6","department6","company6","6666","8(495)6666666","8(916)6666666","address6"),
				new Employee("lastname7 firstname7 middlename7", "email8@mail.ru","title7","department7","company7","7777","8(495)7777777","8(916)7777777","address7"),
				new Employee("lastname8 firstname8 middlename8", "email9@mail.ru","title8","department8","company8","8888","8(495)8888888","8(916)8888888","address8"),
				new Employee("lastname9 firstname9 middlename9", "email0@mail.ru","title9","department9","company9","9999","8(495)9999999","8(916)9999999","address9"),
				new Employee("lastname10 firstname10 middlename10", "email10@mail.ru","title10","department10","company10","0000","8(495)0000000","8(916)0000000","address10")
		));
		List<Employee> testList = this.employeeService.createEmployees(this.employeeList);
		Assertions.assertTrue(testList.size() > 0);
	}

	//@Test
	void deleteAllEmployees(List<Employee> employeeList) {
		this.deleteAllEmployees(employeeList);
		Assertions.assertTrue(true);
	}
}
