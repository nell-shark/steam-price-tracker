package com.nellshark.backend.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.nellshark.backend.TestConfig;
import com.nellshark.backend.models.entities.App;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
  void shouldSaveApp() {
    System.out.println("start");
    App app = App.builder().id(1L).name("Name").type("GAME").headerImage("").isFree(false).build();
    underTest.save(app);
    assertThat(underTest.findAll().size()).isSameAs(1);
  }

  @Test
  void shouldOrderByTypeIgnoringCase() {
    App app1 = App.builder()
        .id(1L).name("app_1").type("dLc").headerImage("").isFree(false).build();
    App app2 = App.builder()
        .id(2L).name("app_2").type("gAmE").headerImage("").isFree(false).build();

    underTest.saveAll(List.of(app1, app2));

    String prefixName = "app";
    Pageable pageable = PageRequest.of(0, 10);
    Page<App> appPage = underTest.findByNameStartsWithIgnoreCaseOrderByType(prefixName, pageable);

    assertThat(appPage).isNotNull();

    List<App> apps = appPage.getContent();

    assertAll(() -> assertThat(apps.size()).isSameAs(2),
        () -> assertThat(apps.getFirst().getName()).isSameAs(app2.getName()),
        () -> assertThat(apps.getLast().getName()).isSameAs(app1.getName()));
  }
}
