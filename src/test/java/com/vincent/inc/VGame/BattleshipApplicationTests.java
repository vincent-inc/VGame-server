package com.vincent.inc.VGame;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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
		time2.increaseSecond(6);
		assertTrue(time1.isBefore(time2));
	}

}
