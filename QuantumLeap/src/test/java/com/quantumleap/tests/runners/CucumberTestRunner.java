package com.quantumleap.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Cucumber TestNG Runner for BDD tests
 * Configures and executes Cucumber feature files via TestNG
 * 
 * @author QuantumLeap Team
 */
@CucumberOptions(
    features = "src/test/resources/features", // Path to feature files
    glue = "com.quantumleap.tests.bdd.stepdefinitions", // Package containing step definitions
    plugin = {
        "pretty", // Pretty format in console
        "html:target/cucumber-reports/html", // HTML reports
        "json:target/cucumber-reports/json/cucumber.json", // JSON reports
        "junit:target/cucumber-reports/xml/cucumber.xml", // JUnit XML reports
        "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" // Allure reports integration
    },
    tags = "@cart", // Run scenarios tagged with @cart
    monochrome = true, // Make console output readable
    publish = false, // Don't publish to Cucumber Reports service
    dryRun = false // Set to true to validate step definitions without execution
)
@Test(groups = {"bdd", "cucumber"})
public class CucumberTestRunner extends AbstractTestNGCucumberTests {
    
    /**
     * Overrides the default data provider to enable parallel execution
     * Each scenario will be executed as a separate test in TestNG
     * 
     * @return Scenarios to execute
     */
    @Override
    @DataProvider(parallel = false) // Set to true for parallel execution
    public Object[][] scenarios() {
        return super.scenarios();
    }
}