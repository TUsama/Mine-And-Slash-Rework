package com.robertx22.age_of_exile.gui.texts.textblocks;

import com.robertx22.age_of_exile.saveclasses.gearitem.gear_bases.TooltipInfo;
import com.robertx22.library_of_exile.wrappers.ExileText;
import lombok.RequiredArgsConstructor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Collections.EMPTY_LIST;

@RequiredArgsConstructor
public class AdditionalBlock extends AbstractTextBlock {

    @Nonnull
    public List<? extends Component> components;
    @Nullable
    public Supplier<Boolean> ifShow = () -> true;

    public AdditionalBlock(@Nonnull Supplier<List<? extends Component>> components) {

        this.components = components.get();
    }


    @Override
    public List<? extends Component> getAvailableComponents() {
        if (ifShow != null && ifShow.get()) {
            ArrayList<MutableComponent> components1 = new ArrayList<>();
            for (Component component : components) {
                components1.add(component.copy());
            }
            components1.add(ExileText.emptyLine().get());
            return components1;
        } else {
            return (List<? extends Component>)EMPTY_LIST;
        }
    }

    public AdditionalBlock showWhen(Supplier<Boolean> condition){
        this.ifShow = condition;
        return this;
    }

}
