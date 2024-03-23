package com.nellshark.backend.repositories;

import static org.junit.jupiter.api.Assertions.assertAll;

import com.nellshark.backend.TestConfig;
import com.nellshark.backend.models.App;
import java.util.List;
import org.junit.jupiter.api.Assertions;
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
  void testFindByNameStartsWithIgnoreCaseOrderByType() {
    App app1 = App.builder().id(1L).name("Game1").type("DLC").headerImage("").isFree(false).build();
    App app2 = App.builder().id(2L).name("Game2").type("dlc").headerImage("").isFree(false).build();
    App app3 = App.builder().id(3L).name("Game3").type("GAME").headerImage("").isFree(false)
        .build();

    underTest.saveAll(List.of(app1, app2, app3));

    String prefixName = "game";
    List<App> apps = underTest.findByNameStartsWithIgnoreCaseOrderByType(prefixName);

    assertAll(
        () -> Assertions.assertEquals(3, apps.size()),
        () -> Assertions.assertEquals(app3.getName(), apps.getFirst().getName()),
        () -> Assertions.assertEquals(app1.getName(), apps.get(1).getName()),
        () -> Assertions.assertEquals(app2.getName(), apps.get(2).getName())
    );
  }
}
