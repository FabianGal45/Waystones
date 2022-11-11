package eu.ovmc.waystones.menusystem;

import eu.ovmc.waystones.menusystem.Menu;
import eu.ovmc.waystones.waystones.PrivateWaystone;

public class EditMenuUtility {

    private PrivateWaystone selected;//This is the waystone a player has selected. Used to edit the waystone.
    private Menu returnToMenu;//This is the menu a player might get redirected to. Usually the last menu they have oppened.

    public EditMenuUtility(PrivateWaystone selected, Menu returnToMenu) {
        this.selected = selected;
        this.returnToMenu = returnToMenu;
    }

    public PrivateWaystone getSelected() {
        return selected;
    }

    public void setSelected(PrivateWaystone selected) {
        this.selected = selected;
    }

    public Menu getReturnToMenu() {
        return returnToMenu;
    }

    public void setReturnToMenu(Menu returnToMenu) {
        this.returnToMenu = returnToMenu;
    }
}
