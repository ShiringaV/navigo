package navigo.app;

/**
 * Created by ASUS 553 on 26.01.2018.
 */

public class ItemModel {

    public int icon;
    public String name;

    // модель данных используемая в адаптере DrawerItemCustomAdapter
    public ItemModel(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }
}