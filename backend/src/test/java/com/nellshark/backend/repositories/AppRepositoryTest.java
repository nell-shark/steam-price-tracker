package com.nellshark.backend.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import com.nellshark.backend.TestConfig;
import com.nellshark.backend.models.App;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AppRepositoryTest {

  @Autowired
  private AppRepository underTest;

  @BeforeEach
  void setUp() {
    underTest.deleteAll();
  }

  @Test
  void check1() {
    System.out.println("start");
    App app = App.builder()
        .id(1L)
        .name("Name")
        .type("GAME")
        .headerImage("")
        .isFree(false)
        .build();
    underTest.save(app);
    assertThat(underTest.findAll().size()).isSameAs(1);
  }

  @Test
  void check2() {
    System.out.println("start");
    App app = App.builder()
        .id(2L)
        .name("Name")
        .type("GAME")
        .headerImage("")
        .isFree(false)
        .build();
    underTest.save(app);
    assertThat(underTest.findAll().size()).isSameAs(2);
  }

}
