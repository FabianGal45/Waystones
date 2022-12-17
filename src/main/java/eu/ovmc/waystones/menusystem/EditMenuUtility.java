package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.menusystem.menu.EditMenu;
import eu.ovmc.waystones.waystones.PrivateWaystone;
import org.bukkit.scheduler.BukkitRunnable;

public class EditMenuUtility {

    private PrivateWaystone selected;//This is the waystone a player has selected. Used to edit the waystone.
    private EditMenu editMenu;//This is the menu a player might get redirected to. Usually the last menu they have oppened.

    public EditMenuUtility(PrivateWaystone selected, EditMenu editMenu) {
        this.selected = selected;
        this.editMenu = editMenu;
    }

    BukkitRunnable runnable = new BukkitRunnable() {
        @Override
        public void run() {
            editMenu.open();
            System.out.println("<><><><><>< TEST ><><><><><>");
        }
    };

    public PrivateWaystone getSelected() {
        return selected;
    }

    public void returnToMenu(){
        System.out.println("EditmenuUtility - Thread: "+ Thread.currentThread().getName()+"; "+Thread.currentThread().getName());
//        runnable.run();
    }
}
