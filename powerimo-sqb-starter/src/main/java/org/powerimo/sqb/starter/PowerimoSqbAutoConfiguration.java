package org.powerimo.sqb.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;

import javax.sql.DataSource;

@AutoConfiguration
@Slf4j
@RequiredArgsConstructor
@AutoConfigureAfter({DataSource.class})
public class PowerimoSqbAutoConfiguration {

}
