package com.marcblais.scrabbleapi.controllers;

import com.marcblais.scrabbleapi.dto.*;
import com.marcblais.scrabbleapi.encryption.PlayerToken;
import com.marcblais.scrabbleapi.entities.*;
import com.marcblais.scrabbleapi.services.GameService;
import com.marcblais.scrabbleapi.utilities.DictionnaryEntriesFinder;
import com.marcblais.scrabbleapi.utilities.PointCalculator;
import com.marcblais.scrabbleapi.utilities.ThreadsRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("/grid/new")
    public ResponseEntity<Long> createGrid(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GameOption gameOption
    ) {
        Player player = findPlayer(token);

        if (player == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        if (player.getGrids().size() > 7)
            return new ResponseEntity<>(HttpStatus.INSUFFICIENT_STORAGE);

        GridDTO gridDTO = new GridDTO();
        Grid grid;
        gridDTO.buildGrid();
        gridDTO.setBlankTiles(new Integer[][]{});
        gridDTO.setGridType(gameOption.getGridType());
        gridDTO.setLanguage(gameOption.getLanguage());
        gridDTO.setName(gameOption.getName().toUpperCase());
        gridDTO.setPlayerLetters(new String[]{"","","","","","",""});
        gridDTO.setPlayer(player);
        grid = gridDTO.toGrid();
        grid.setLastUpdate(LocalDateTime.now());
        player.getGrids().add(grid);
        player.getGrids().sort(Grid::compareTo);

        gameService.saveGrid(grid);
        return new ResponseEntity<>(grid.getId(), HttpStatus.OK);
    }

    @PostMapping("/grid")
    public ResponseEntity<Long> saveGame(
            @CookieValue(value = "token", required = false) String token,
            @RequestBody GridDTO gridDTO
    ) {
        Player player = findPlayer(token);
        Grid newGrid = gridDTO.toGrid();
        Grid grid;

        if (player == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        grid = player.findGrid(newGrid.getId());
        if (grid == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        grid.setGrid(newGrid.getGrid());
        grid.setPlayerLetters(newGrid.getPlayerLetters());
        grid.setBlankTiles(newGrid.getBlankTiles());
        grid.setLastUpdate(LocalDateTime.now());
        player.getGrids().sort(Grid::compareTo);
        gameService.saveGrid(grid);
        return new ResponseEntity<>(grid.getId(), HttpStatus.OK);
    }

    @DeleteMapping("/grid/{id}")
    public ResponseEntity<Void> deleteGame(
            @CookieValue(value = "token", required = false) String token,
            @PathVariable(name = "id") long id
    ) {
        if (token == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        Player player = findPlayer(token);
        Grid grid;

        if (player == null)
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        grid = player.findGrid(id);
        if (grid == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        player.getGrids().remove(grid);
        this.gameService.deleteGrid(grid);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/grid/{id}")
    public ResponseEntity<GridDTO> getGame(
            @CookieValue(name = "token") String token,
            @PathVariable(name = "id") Long id
    ) {
        Player player = findPlayer(token);
        if (player == null)
            return new ResponseEntity<>(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);

        Grid grid = player.findGrid(id);
        if (grid == null)
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(new GridDTO(grid), HttpStatus.OK);
    }

    private Player findPlayer(String token) {
        String username = PlayerToken.getUsernameFromJwt(token);
        return gameService.findPlayerByUsername(username);
    }
}
