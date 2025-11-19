package com.quantumleap.tests.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Smoke Test Runner for critical BDD scenarios
 * Executes only the most critical scenarios tagged with @smoke
 * 
 * @author QuantumLeap Team
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "com.quantumleap.tests.bdd.stepdefinitions",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/smoke-html",
        "json:target/cucumber-reports/json/smoke-cucumber.json",
        "junit:target/cucumber-reports/xml/smoke-cucumber.xml"
    },
    tags = "@smoke and @bdd", // Only smoke tests that are BDD
    monochrome = true,
    publish = false
)
@Test(groups = {"smoke", "bdd"})
public class SmokeTestRunner extends AbstractTestNGCucumberTests {
    
    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}