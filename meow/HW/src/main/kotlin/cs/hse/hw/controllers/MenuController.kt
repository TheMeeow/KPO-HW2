package cs.hse.hw.controllers

import cs.hse.hw.dataComponents.Dish
import cs.hse.hw.dataComponents.DishData
import cs.hse.hw.services.MenuService
import cs.hse.hw.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/menu")
class MenuController(private val menuService: MenuService,
                     private val userService: UserService) {
    private var response = ""

    @GetMapping()
    fun showMenu(): ResponseEntity<String> {
        response = menuService.getMenu()
        return ResponseEntity.ok(response)
    }

    @PostMapping("/addDish")
    fun addDish(@RequestBody dish: DishData,
                @RequestParam countOfPortions: Int): ResponseEntity<String> {
        if (dish.name.contains("ERROR")) {
            return ResponseEntity.badRequest().body("Illegal name for the dish!")
        }
        try {
            userService.tryGetAdminAccess()
            response = menuService.tryToAddDishToMenu(
                    Dish(dish.name, dish.price, dish.cookingTime), countOfPortions)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/deleteDish")
    fun delDish(@RequestParam dishName: String): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = menuService.tryToDeleteDishFromMenu(dishName)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/changeCountOfPortionsForDish")
    fun changeDishCount(@RequestParam dishName: String,
                        @RequestParam newCountOfPortions: Int): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = menuService.tryToChangeDishCount(dishName, newCountOfPortions)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/changeDishPrice")
    fun changeDishPrice(@RequestParam dishName: String,
                        @RequestParam newPrice: Int): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = menuService.tryToChangeDishPrice(dishName, newPrice)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/changeDishCookingTime")
    fun changeDishCookingTime(@RequestParam dishName: String,
                              @RequestParam newCookingTime: Int): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = menuService.tryToChangeDishCookingTime(dishName, newCookingTime)
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }

    @PostMapping("/saveCurrentMenu")
    fun saveCurrentMenu(): ResponseEntity<String> {
        try {
            userService.tryGetAdminAccess()
            response = menuService.serialiseMenu()
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(e.message)
        }
        if (response.contains("ERROR")) {
            return ResponseEntity.badRequest().body(response)
        }
        return ResponseEntity.ok(response)
    }
}