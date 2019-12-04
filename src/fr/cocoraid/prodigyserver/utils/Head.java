package fr.cocoraid.prodigyserver.utils;

import org.bukkit.inventory.ItemStack;

public enum Head {

    //PIRATE
    BATTLESHIP_SPARROW(SkullCreator.itemFromBase64("ZmQ5MTllMTZlYmI5ZWJkZGYxYmU2NDQ5N2JmMjZlZWRhYjJiMTVjMDI4YjViZjk3ZDJmZmFlN2Y3ZDc0OWM3In19fQ==")),
    BATTLESHIP_DAVY_JONES(SkullCreator.itemFromBase64("MzQ5N2JjYzY1NWQ1YjA0NjE1ZjkxYThlN2MyY2ZjYjJiNDYyYmM0MjlhN2QwNzJhY2RlNGQ2ZjhiZDRmNTMifX19")),
    BATTLESHIP_GIRL(SkullCreator.itemFromBase64("N2M0NjUzODZiYmU5ZmFmMTQzN2FmNjM3N2Q2NjczNWRjNWExNWVhNWNlZGYyNmJkOTVmZDNmZTY2YjNhZmNkIn19fQ==")),
    BATTLESHIP_VILLAGER(SkullCreator.itemFromBase64("ZTBjZjhjYWViNWI4OTYxODg4YmQ4NmZhOTgzNjk0YmYxNTFmOWEzZTU1NzVjZGQwNDA5MDYzMWZlZTUwYmNjNiJ9fX0=")),
    BATTLESHIP_CLASSIC(SkullCreator.itemFromBase64("NzhiOGQzNWU1ZjY2MjE2MGVhMDJiOWM2NzQyZWMxNzlmYmVkOTc3NWM4M2UyZWEyMThmMmIwNDkxNTkyY2RmZCJ9fX0=")),
    BATTLESHIP_OLD(SkullCreator.itemFromBase64("NmEyMjIwMjMyYmI4MTkyOWE2NmIzYjk1MThiNGVkYTFlYTRlYmE0YTk1YjE0YWUxOTM3NjlmNTU1ZDcyIn19fQ==")),
    BATTLESHIP_YOUNG(SkullCreator.itemFromBase64("OTg0NTAxYzQzNmI1ZTczZjU2MTY0YzA5MjNmOTY4NzY2ZDE2M2E3YTA1MWM4YmE4NDQ0Y2U2ZjVkZmFjZmVlIn19fQ==")),
    BATTLESHIP_ASIAN(SkullCreator.itemFromBase64("OTE0ODlmM2ZhZDc0N2MwOWZjNDE2ZDViZjc3NDQyZmQ1MGMwYmM1ZTQwODNmY2Q4YzEyNDhhZjAyYTE3YjJiYiJ9fX0=")),
    BATTLESHIP_CAPTAIN(SkullCreator.itemFromBase64("Nzc4ODI4YWMzYTYxZDg3MTJkZTUyNzFiYjM1YzRjNzE0NmE2YjM2YzZiNGU1NzZmNWViOGQxNzhkYTdkZmQzNCJ9fX0=")),
    BATTLESHIP_OTHER(SkullCreator.itemFromBase64(" Y2E1ZDdkMzllZmM5ZTRkYWQ5MmJkYmM3YmVhMGJmNmRjMWE5ZGEzZDYxZmZiZGI0MTljYWRjYmQxMTdlMTMifX19"));






    private ItemStack head;
    Head(ItemStack head) {
        this.head = head;
    }

    public ItemStack getHead() {
        return head;
    }
    }
