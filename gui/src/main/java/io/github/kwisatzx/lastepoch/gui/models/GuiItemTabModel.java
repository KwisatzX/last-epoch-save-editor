package io.github.kwisatzx.lastepoch.gui.models;

import io.github.kwisatzx.lastepoch.gui.controllers.GuiItemTabController;
import io.github.kwisatzx.lastepoch.gui.views.elements.SelectionWrapper;
import io.github.kwisatzx.lastepoch.itemdata.item.AbstractItem;
import io.github.kwisatzx.lastepoch.itemdata.item.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiItemTabModel extends GuiTabModel {
    private final GuiItemTabController controller;
    private final SelectionWrapper selection;

    public GuiItemTabModel(GuiItemTabController controller) {
        super(controller);
        this.controller = controller;
        selection = controller.getSelection();
    }

    public void setItem() {
        selection.ifItemPresent(item -> {
            Item itemSettings = controller.getUiItemSettingsFromView();
            item.setItemType(itemSettings.getItemType());
            item.setItemTier(itemSettings.getItemTier());
            item.setImplicitValue1(itemSettings.getImplicitValue1());
            item.setImplicitValue2(itemSettings.getImplicitValue2());
            item.setImplicitValue3(itemSettings.getImplicitValue3());
            item.setAffixNumberVisual(itemSettings.getAffixNumberVisual());
            item.setInstability(itemSettings.getInstability());
            controller.setCharaEquipment();
            controller.reloadTreeView();
        });
    }

    public Item getItemCopy(Item original) {
        List<AbstractItem.AffixData> newAffixList = new ArrayList<>();
        for (int i = 0; i < original.getAffixList().size(); i++) {
            AbstractItem.AffixData toCopy = original.getAffixList().get(i);
            newAffixList.add(new AbstractItem.AffixData(toCopy.tier, toCopy.type, toCopy.value));
        }

        return new AbstractItem(original.getItemType(),
                                original.getItemTier(),
                                original.getAffixNumberVisual(),
                                original.getImplicitValue1(),
                                original.getImplicitValue2(),
                                original.getImplicitValue3(),
                                original.getInstability(),
                                original.getAffixNumber(),
                                newAffixList);
    }
}
