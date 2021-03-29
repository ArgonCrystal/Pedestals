package com.mowmaster.pedestals.item;

import com.mowmaster.pedestals.blocks.PedestalBlock;
import com.mowmaster.pedestals.item.pedestalFilters.ItemFilterBase;
import com.mowmaster.pedestals.references.Reference;
import com.mowmaster.pedestals.tiles.PedestalTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import static com.mowmaster.pedestals.pedestals.PEDESTALS_TAB;
import static com.mowmaster.pedestals.references.Reference.MODID;

public class ItemFilterSwapper extends Item {

    public ItemFilterSwapper() {
        super(new Properties().maxStackSize(1).containerItem(FILTERTOOL).group(PEDESTALS_TAB));
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return new ItemStack(this.getItem());
    }

    @Override
    public ActionResultType onItemUse(ItemUseContext context) {
        World worldIn = context.getWorld();
        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getPos();
        ItemStack stackInMainHand = player.getHeldItemMainhand();
        ItemStack stackInOffHand = player.getHeldItemOffhand();

        TranslationTextComponent filterRemove = new TranslationTextComponent(Reference.MODID + ".filters.insert_remove");
        TranslationTextComponent filterSwitch = new TranslationTextComponent(Reference.MODID + ".filters.insert_switch");
        TranslationTextComponent filterInsert = new TranslationTextComponent(Reference.MODID + ".filters.insert_insert");

        if(!worldIn.isRemote)
        {
            BlockState getBlockState = worldIn.getBlockState(pos);
            if(getBlockState.getBlock() instanceof PedestalBlock) {
                TileEntity tile = worldIn.getTileEntity(pos);
                if(tile instanceof PedestalTileEntity)
                {
                    PedestalTileEntity pedestal = ((PedestalTileEntity)worldIn.getTileEntity(pos));
                    if(pedestal.hasFilter())
                    {
                        ItemFilterBase getFilter = (ItemFilterBase)pedestal.getFilterInPedestal().getItem();
                        getFilter.chatDetails(player,pedestal);
                        return ActionResultType.SUCCESS;
                    }

                    return ActionResultType.FAIL;
                }
            }
        }

        return super.onItemUse(context);
    }

    /*else if(getItemInHand instanceof ItemFilterSwapper)
    {
        if(player.isCrouching())
        {

        }
        else
        {

        }

        return ActionResultType.FAIL;
    }*/


    public static final Item FILTERTOOL = new ItemFilterSwapper().setRegistryName(new ResourceLocation(MODID, "filterswapper"));

    @SubscribeEvent
    public static void onItemRegistryReady(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(FILTERTOOL);
    }




}
