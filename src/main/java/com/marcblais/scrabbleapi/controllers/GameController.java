package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/grid/new")
    public ResponseEntity<String> createGrid(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GameOption gameOption
    ) {
        Player player = findPlayer(token);

        if (player == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (player.getGrids().size() > 7)
            return new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE);

        GridDTO gridDTO = GridDTO.builder()
                .gridType(gameOption.getGridType())
                .name(gameOption.getName().toUpperCase())
                .language(gameOption.getLanguage())
                .player(player)
                .build();
        gridDTO.buildGrid();

        Grid grid;
        grid = gridDTO.toGrid();
        grid.setUuid(UUID.randomUUID().toString());
        grid.setLastUpdate(LocalDateTime.now());

        gameService.saveGrid(grid);
        return new ResponseEntity<>(grid.getUuid(), HttpStatus.OK);
    }

    @PostMapping("/grid/{uuid}")
    public ResponseEntity<String> saveGame(
            @CookieValue(value = "token", required = false) String token,
            @PathVariable(name = "uuid") String uuid,
            @RequestBody GridDTO gridDTO
    ) {
        Player player = findPlayer(token);
        Grid newGrid = gridDTO.toGrid();
        Grid grid;

        if (player == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        grid = player.findGrid(uuid);
        if (grid == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        grid.setGrid(newGrid.getGrid());
        grid.setPlayerLetters(newGrid.getPlayerLetters());
        grid.setGridType(newGrid.getGridType());
        grid.setLanguage(newGrid.getLanguage());
        grid.setName(newGrid.getName().toUpperCase());
        grid.setBlankTiles(newGrid.getBlankTiles());
        grid.setLastUpdate(LocalDateTime.now());

        gameService.saveGrid(grid);
        return new ResponseEntity<>(grid.getUuid(), HttpStatus.OK);
    }

    @DeleteMapping("/grid/{uuid}")
    public ResponseEntity<Void> deleteGame(
            @CookieValue(value = "token", required = false) String token,
            @PathVariable(name = "uuid") String uuid
    ) {
        if (token == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Player player = findPlayer(token);
        Grid grid;

        if (player == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        grid = player.findGrid(uuid);
        if (grid == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        player.getGrids().remove(grid);
        this.gameService.deleteGrid(grid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/grid/{uuid}")
    public ResponseEntity<GridDTO> getGame(
            @CookieValue(name = "token") String token,
            @PathVariable(name = "uuid") String uuid
    ) {
        Player player = findPlayer(token);
        if (player == null)
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

        Grid grid = player.findGrid(uuid);
        if (grid == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new GridDTO(grid), HttpStatus.OK);
    }

    private Player findPlayer(String token) {
        String username = PlayerToken.getUsernameFromJwt(token);
        return gameService.findPlayerByUsername(username);
    }
}
