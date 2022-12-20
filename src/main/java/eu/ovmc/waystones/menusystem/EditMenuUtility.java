package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.menusystem.menu.EditMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.scheduler.BukkitRunnable;

public class EditMenuUtility {

    private PrivateWaystone selected;//This is the waystone a player has selected. Used to edit the waystone.
    private Menu menu;//This is the menu a player might get redirected to. Usually the last menu they have oppened.

    public EditMenuUtility(PrivateWaystone selected, Menu menu) {
        this.selected = selected;
        this.menu = menu;
    }

    BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            menu.open();
            System.out.println("<><><><><>< TEST ><><><><><>");
        }
    };

    public PrivateWaystone getSelected() {
        return selected;
    }

    public void returnToMenu(){
        System.out.println("EditmenuUtility - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());
//        menu.open();
//        runnable.run();
    }
}
