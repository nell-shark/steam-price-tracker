package com.nellshark.backend.repositories;

import static org.junit.jupiter.api.Assertions.assertAll;

import com.nellshark.backend.TestConfig;
import com.nellshark.backend.models.Game;
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
class GameRepositoryTest {

  @Autowired
  private GameRepository underTest;

  @BeforeEach
  void setUp() {
    underTest.deleteAll();
  }

  @Test
  void testFindByNameStartsWithIgnoreCaseOrderByGameType() {
    Game game1 = Game.builder().id(1L).name("Game1").gameType("DLC").headerImage("").build();
    Game game2 = Game.builder().id(2L).name("Game2").gameType("dlc").headerImage("").build();
    Game game3 = Game.builder().id(3L).name("Game3").gameType("GAME").headerImage("").build();

    underTest.saveAll(List.of(game1, game2, game3));

    String prefixName = "game";
    List<Game> games = underTest.findByNameStartsWithIgnoreCaseOrderByGameType(prefixName);

    assertAll(
        () -> Assertions.assertEquals(3, games.size()),
        () -> Assertions.assertEquals(game3.getName(), games.getFirst().getName()),
        () -> Assertions.assertEquals(game1.getName(), games.get(1).getName()),
        () -> Assertions.assertEquals(game2.getName(), games.get(2).getName())
    );
  }
}
