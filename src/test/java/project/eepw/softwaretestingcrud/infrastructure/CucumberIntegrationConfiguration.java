package project.eepw.softwaretestingcrud.infrastructure;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
	features = "classpath:features/",
	plugin = { "json:target/cucumber-report.json" },
	glue = { "project.eepw.softwaretestingcrud.cucumber" }
)
public class CucumberIntegrationConfiguration {}
