package com.vincent.inc.VGame.schedule;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.ObjectUtils;

import com.vincent.inc.VGame.model.Lobby;
import com.vincent.inc.VGame.service.LobbyService;

@Configuration
@EnableScheduling
public class CleanUpSchedule {
    private final long DELAY = 1000; //1s
    private final long INITIAL_DELAY = 60000; //60s

    @Autowired
    private LobbyService lobbyService;

    private ExecutorService threadPool;

    public CleanUpSchedule() {
        this.threadPool = Executors.newCachedThreadPool();
    }

    public Future<?> addTask(Callable<?> task) {
        return this.threadPool.submit(task);
    }

    @Scheduled(fixedDelay = DELAY, initialDelay = INITIAL_DELAY)
    public void cleanUpLobby() {
        var lobbies = lobbyService.getAll();

        if(!ObjectUtils.isEmpty(lobbies)) {
            lobbies.forEach(l -> {
                this.addTask(new CleanLobby(l, this.lobbyService));
            });
        }    
    }

    class CleanLobby implements Callable<Lobby> {
        private Lobby lobby;
        private LobbyService lobbyService;

        public CleanLobby(Lobby lobby, LobbyService lobbyService) {
            this.lobby = lobby;
            this.lobbyService = lobbyService;
        }

        @Override
        public Lobby call() throws Exception {
            this.lobbyService.autoLeaveOverdueUser(lobby);

            return lobby;
        }

    }
}
