package com.vincent.inc.VGame;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.google.gson.Gson;
import com.vincent.inc.VGame.util.Time;

@SpringBootTest
class BattleshipApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void timeTest() {
		Time time1 = new Time();
		Time time2 = new Time();
		time1.increaseSecond(5);
		time2.increaseSecond(4);
		assertTrue(time1.isBefore(time2));
	}

}
