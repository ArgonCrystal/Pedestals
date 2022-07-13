package com.mowmaster.pedestals.Items.Upgrades.Pedestal;

import com.mowmaster.mowlib.MowLibUtils.ColorReference;
import com.mowmaster.mowlib.MowLibUtils.MowLibFakePlayer;
import com.mowmaster.mowlib.MowLibUtils.OwnerUtil;
import com.mowmaster.pedestals.Blocks.Pedestal.BasePedestalBlockEntity;
import com.mowmaster.pedestals.Items.Filters.BaseFilter;
import com.mowmaster.pedestals.Items.Filters.IPedestalFilter;
import com.mowmaster.pedestals.PedestalTab.PedestalsTab;
import com.mowmaster.pedestals.PedestalUtils.PedestalModesAndTypes;
import com.mowmaster.pedestals.PedestalUtils.PedestalUtilities;
import com.mowmaster.pedestals.Registry.DeferredRegisterItems;

import static com.mowmaster.pedestals.Blocks.Pedestal.BasePedestalBlock.FACING;
import static com.mowmaster.pedestals.PedestalUtils.PedestalModesAndTypes.getModeLocalizedString;
import static com.mowmaster.pedestals.PedestalUtils.References.MODID;


import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import com.mowmaster.mowlib.MowLibUtils.MessageUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;




import net.minecraft.world.item.Item.Properties;

public class ItemUpgradeBase extends Item implements IPedestalUpgrade
{
    public ItemUpgradeBase(Properties p_41383_) {
        super(new Properties().tab(PedestalsTab.TAB_ITEMS));
    }

    /*
    *
    * Methods Runs By Pedestal
    * START
    *
     */
    @Override
    public int getComparatorRedstoneLevel(Level worldIn, BlockPos pos) {
        return PedestalUtilities.getRedstoneLevelPedestal(worldIn, pos);
    }

    @Override
    public void updateAction(Level world, BasePedestalBlockEntity pedestal) {

    }

    @Override
    public void actionOnCollideWithBlock(BasePedestalBlockEntity pedestal, Entity entityIn) {

    }

    @Override
    public void actionOnNeighborBelowChange(BasePedestalBlockEntity pedestal, BlockPos belowBlock) {

    }

    @Override
    public void actionOnRemovedFromPedestal(BasePedestalBlockEntity pedestal, ItemStack coinInPedestal) {

    }

    /*
     *
     * Methods Runs By Pedestal
     * END
     *
     */


    /*
    MODES

    0 - Items
    1 - Fluids
    2 - Energy
    3 - XP
    4 - Dust
     */

    public static void writeTransportModeToNBT(ItemStack filterStack, int mode, boolean allowed) {
        CompoundTag compound = new CompoundTag();
        if(filterStack.hasTag())
        {
            compound = filterStack.getTag();
        }
        compound.putBoolean(MODID + "_" + PedestalModesAndTypes.getModeStringFromInt(mode)+"_transport_mode",allowed);
        filterStack.setTag(compound);
    }

    public static boolean getTransportModeFromNBT(ItemStack filterStack, int mode) {
        boolean allowed = true;
        if(filterStack.hasTag())
        {
            CompoundTag getCompound = filterStack.getTag();
            String tag = MODID + "_" + PedestalModesAndTypes.getModeStringFromInt(mode)+"_transport_mode";
            if(filterStack.getTag().contains(tag))
            {
                allowed = getCompound.getBoolean(tag);
            }
        }
        return allowed;
    }

    public void toggleTransportMode(Player player, ItemStack heldItem, InteractionHand hand) {
        if(heldItem.getItem() instanceof ItemUpgradeBase baseUpgrade)
        {
            int mode = getUpgradeMode(heldItem);
            boolean getTransportMode = getTransportModeFromNBT(heldItem,mode);
            writeTransportModeToNBT(heldItem, mode, !getTransportMode);
            player.setItemInHand(hand,heldItem);

            ChatFormatting colorChange = (!getTransportMode)?(ChatFormatting.WHITE):(ChatFormatting.BLACK);
            MessageUtils.messagePopup(player,colorChange,MODID + ((!getTransportMode)?(".transport_mode_changed_true"):(".transport_mode_changed_false")));
        }
    }

    public void incrementUpgradeMode(Player player, ItemStack heldItem, InteractionHand hand)
    {
        if(heldItem.getItem() instanceof ItemUpgradeBase baseUpgrade)
        {
            int mode = getUpgradeMode(heldItem)+1;
            int setNewMode = (mode<=4)?(mode):(0);
            saveUpgradeModeToNBT(heldItem,setNewMode);
            player.setItemInHand(hand,heldItem);

            ChatFormatting colorChange = PedestalModesAndTypes.getModeDarkColorFormat(setNewMode);
            String typeString = getModeLocalizedString(setNewMode);

            List<String> listed = new ArrayList<>();
            listed.add(MODID + typeString);
            MessageUtils.messagePopupWithAppend(MODID, player,colorChange,MODID + ".mode_changed",listed);
        }
    }

    public static void saveUpgradeModeToNBT(ItemStack augment, int mode)
    {
        CompoundTag compound = new CompoundTag();
        if(augment.hasTag())
        {
            compound = augment.getTag();
        }
        compound.putInt(MODID+"_upgrade_mode",mode);
        augment.setTag(compound);
    }

    public static int readUpgradeModeFromNBT(ItemStack augment) {
        if(augment.hasTag())
        {
            CompoundTag getCompound = augment.getTag();
            return getCompound.getInt(MODID+"_upgrade_mode");
        }
        return 0;
    }

    public static int getUpgradeMode(ItemStack stack) {

        return readUpgradeModeFromNBT(stack);
    }

    public static int getUpgradeModeForRender(ItemStack stack) {

        int mode = readUpgradeModeFromNBT(stack);
        boolean type = getTransportModeFromNBT(stack,mode);
        return (type)?(mode):(mode+5);
    }

    public static MutableComponent getUpgradeModeComponentFromInt(int mode) {

        switch(mode)
        {
            case 0: return Component.translatable(MODID + ".item_mode_component");
            case 1: return Component.translatable(MODID + ".fluid_mode_component");
            case 2: return Component.translatable(MODID + ".energy_mode_component");
            case 3: return Component.translatable(MODID + ".xp_mode_component");
            case 4: return Component.translatable(MODID + ".dust_mode_component");
            default: return Component.translatable(MODID + ".item_mode_component");
        }
    }






    public static void saveBlockPosToNBT(ItemStack upgrade, int num, BlockPos posToSave)
    {
        CompoundTag compound = new CompoundTag();
        if(upgrade.hasTag())
        {
            compound = upgrade.getTag();
        }
        List<Integer> listed = new ArrayList<>();
        listed.add(posToSave.getX());
        listed.add(posToSave.getY());
        listed.add(posToSave.getZ());
        compound.putIntArray(MODID+"_upgrade_blockpos_"+num, listed);
        upgrade.setTag(compound);
    }

    public static BlockPos readBlockPosFromNBT(ItemStack upgrade, int num) {
        if(upgrade.hasTag())
        {
            String tag = MODID+"_upgrade_blockpos_"+num;
            CompoundTag getCompound = upgrade.getTag();
            if(upgrade.getTag().contains(tag))
            {
                int[] listed = getCompound.getIntArray(tag);
                if(listed.length>=3)return new BlockPos(listed[0],listed[1],listed[2]);
            }
        }
        return BlockPos.ZERO;
    }

    public static BlockPos getBlockPosOnUpgrade(ItemStack stack, int num) {

        return readBlockPosFromNBT(stack,num);
    }

    public boolean hasTwoPointsSelected(ItemStack stack)
    {
        return !readBlockPosFromNBT(stack,1).equals(BlockPos.ZERO) && !readBlockPosFromNBT(stack,2).equals(BlockPos.ZERO);
    }

    public AABB getAABBonUpgrade(ItemStack stack)
    {
        if(stack.getItem() instanceof ISelectableArea && hasTwoPointsSelected(stack))
        {
            BlockPos posOne = readBlockPosFromNBT(stack,1);
            BlockPos posTwo = readBlockPosFromNBT(stack,2);

            return new AABB(Math.min(posOne.getX(), posTwo.getX()),Math.min(posOne.getY(), posTwo.getY()),Math.min(posOne.getZ(), posTwo.getZ()),
                    Math.max(posOne.getX(), posTwo.getX()),Math.max(posOne.getY(), posTwo.getY()),Math.max(posOne.getZ(), posTwo.getZ()));
        }
        return new AABB(BlockPos.ZERO);
    }

    public boolean selectedAreaWithinRange(BasePedestalBlockEntity pedestal)
    {
        if(pedestal.isPedestalInRange(pedestal, readBlockPosFromNBT(pedestal.getCoinOnPedestal(),1)) && pedestal.isPedestalInRange(pedestal, readBlockPosFromNBT(pedestal.getCoinOnPedestal(),2)))
        {
            return true;
        }

        return false;
    }

    /*
    INTERACTIONS

    MODE CHANGE:
    - (Offhand)Crouch Right Click = Mode change
    TYPE CHANGE:
    - (Offhand)Right Click = Type Change



    GET BLOCK POS FOR AREA:
    main hand crouch right click to start it and right click to end it???
     */

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        Level level = p_41432_;
        Player player = p_41433_;
        InteractionHand hand = p_41434_;
        ItemStack itemInHand = player.getItemInHand(hand);
        ItemStack itemInOffhand = player.getOffhandItem();
        HitResult result = player.pick(5,0,false);

        //result.getType().equals(HitResult.Type.MISS)

        if(itemInHand.getItem() instanceof ItemUpgradeBase)
        {
            if(hand.equals(InteractionHand.MAIN_HAND) && itemInHand.getItem() instanceof ISelectableArea)
            {
                if(result.getType().equals(HitResult.Type.BLOCK))
                {
                    BlockPos atLocation = new BlockPos(result.getLocation().x, result.getLocation().y, result.getLocation().z);
                    if(player.isCrouching())
                    {
                        saveBlockPosToNBT(itemInHand,1,atLocation);
                        player.setItemInHand(hand,itemInHand);
                        MessageUtils.messagePopup(player,ChatFormatting.WHITE,MODID + ".upgrade_blockpos_first");
                    }
                    else
                    {
                        saveBlockPosToNBT(itemInHand,2,atLocation);
                        player.setItemInHand(hand,itemInHand);
                        MessageUtils.messagePopup(player,ChatFormatting.WHITE,MODID + ".upgrade_blockpos_second");
                    }
                }
                else if(result.getType().equals(HitResult.Type.MISS))
                {
                    saveBlockPosToNBT(itemInHand,1,BlockPos.ZERO);
                    saveBlockPosToNBT(itemInHand,2,BlockPos.ZERO);
                    MessageUtils.messagePopup(player,ChatFormatting.WHITE,MODID + ".upgrade_blockpos_clear");
                }
            }
            else if(hand.equals(InteractionHand.OFF_HAND) && itemInHand.getItem() instanceof IHasModeTypes)
            {
                if(result.getType().equals(HitResult.Type.MISS))
                {
                    if(player.isCrouching())
                    {
                        incrementUpgradeMode(player,itemInOffhand,hand);
                    }
                    else
                    {
                        toggleTransportMode(player,itemInOffhand,hand);
                    }
                }
            }

        }

        return InteractionResultHolder.fail(p_41433_.getItemInHand(p_41434_));
    }

    @Override
    public boolean canTransferItems(ItemStack upgrade)
    {
        return getTransportModeFromNBT(upgrade, 0);
    }

    @Override
    public boolean canTransferFluids(ItemStack upgrade)
    {
        return getTransportModeFromNBT(upgrade, 1);
    }

    @Override
    public boolean canTransferEnergy(ItemStack upgrade)
    {
        return getTransportModeFromNBT(upgrade, 2);
    }

    @Override
    public boolean canTransferXP(ItemStack upgrade)
    {
        return getTransportModeFromNBT(upgrade, 3);
    }

    @Override
    public boolean canTransferDust(ItemStack upgrade)
    {
        return getTransportModeFromNBT(upgrade, 4);
    }

    public BlockPos getPosOfBlockBelow(Level world, BlockPos posOfPedestal, int numBelow)
    {
        BlockState state = world.getBlockState(posOfPedestal);

        Direction enumfacing = (state.hasProperty(FACING))?(state.getValue(FACING)):(Direction.UP);
        BlockPos blockBelow = posOfPedestal;
        switch (enumfacing)
        {
            case UP:
                return blockBelow.offset(0,-numBelow,0);
            case DOWN:
                return blockBelow.offset(0,numBelow,0);
            case NORTH:
                return blockBelow.offset(0,0,numBelow);
            case SOUTH:
                return blockBelow.offset(0,0,-numBelow);
            case EAST:
                return blockBelow.offset(-numBelow,0,0);
            case WEST:
                return blockBelow.offset(numBelow,0,0);
            default:
                return blockBelow;
        }
    }

    public Direction getPedestalFacing(Level world, BlockPos posOfPedestal)
    {
        BlockState state = world.getBlockState(posOfPedestal);
        return state.getValue(FACING);
    }

    public int getItemTransferRate(ItemStack stack)
    {
        int transferRate = 1;
        int speed = 1;
        switch (speed)
        {
            case 0:
                transferRate = 1;
                break;
            case 1:
                transferRate=4;
                break;
            case 2:
                transferRate = 8;
                break;
            case 3:
                transferRate = 16;
                break;
            case 4:
                transferRate = 32;
                break;
            case 5:
                transferRate=64;
                break;
            default: transferRate=1;
        }

        return  transferRate;
    }

    public boolean isInventoryEmpty(LazyOptional<IItemHandler> cap)
    {
        if(cap.isPresent())
        {
            IItemHandler handler = cap.orElse(null);
            if(handler != null)
            {
                int range = handler.getSlots();

                ItemStack itemFromInv = ItemStack.EMPTY;
                itemFromInv = IntStream.range(0,range)//Int Range
                        .mapToObj((handler)::getStackInSlot)//Function being applied to each interval
                        .filter(itemStack -> !itemStack.isEmpty())
                        .findFirst().orElse(ItemStack.EMPTY);

                if(!itemFromInv.isEmpty())
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean doItemsMatch(ItemStack stackPedestal, ItemStack itemStackIn)
    {
        return ItemHandlerHelper.canItemStacksStack(stackPedestal,itemStackIn);
    }

    public int getNextSlotWithItemsCapFiltered(BasePedestalBlockEntity pedestal, LazyOptional<IItemHandler> cap)
    {
        AtomicInteger slot = new AtomicInteger(-1);
        if(cap.isPresent()) {

            cap.ifPresent(itemHandler -> {
                int range = itemHandler.getSlots();
                for(int i=0;i<range;i++)
                {
                    ItemStack stackInSlot = itemHandler.getStackInSlot(i);
                    //find a slot with items
                    if(!stackInSlot.isEmpty())
                    {
                        //check if it could pull the item out or not
                        if(!itemHandler.extractItem(i,1 ,true ).equals(ItemStack.EMPTY))
                        {
                            //If pedestal is empty accept any items
                            if(passesItemFilter(pedestal,stackInSlot))
                            {
                                ItemStack itemFromPedestal = pedestal.getMatchingItemInPedestalOrEmptySlot(stackInSlot);
                                if(itemFromPedestal.isEmpty())
                                {
                                    slot.set(i);
                                    break;
                                }
                                //if stack in pedestal matches items in slot
                                else if(doItemsMatch(itemFromPedestal,stackInSlot))
                                {
                                    slot.set(i);
                                    break;
                                }
                            }
                        }
                    }
                }});


        }

        return slot.get();
    }

    public int getNextSlotEmptyOrMatching(LazyOptional<IItemHandler> cap, ItemStack stackInPedestal)
    {
        AtomicInteger slot = new AtomicInteger(-1);
        if(cap.isPresent())
        {
            IItemHandler handler = cap.orElse(null);
            if(handler != null)
            {
                int range = handler.getSlots();
                for(int i=0;i<range;i++)
                {
                    ItemStack stackInSlot = handler.getStackInSlot(i);
                    int maxSizeSlot = handler.getSlotLimit(i);
                    if(maxSizeSlot>0)
                    {
                        if(stackInSlot.getMaxStackSize()>1)
                        {
                            if(doItemsMatch(stackInSlot,stackInPedestal) && stackInSlot.getCount() < handler.getSlotLimit(i))
                            {
                                slot.set(i);
                                break;
                            }
                            else if(stackInSlot.isEmpty())
                            {
                                slot.set(i);
                                break;
                            }
                            //if chest is full
                            else if(i==range)
                            {
                                slot.set(i);
                            }
                        }
                    }
                }
            }
        }
        return slot.get();
    }

    public boolean passesItemFilter(BasePedestalBlockEntity pedestal, ItemStack stackIn)
    {
        boolean returner = true;
        if(pedestal.hasFilter())
        {
            Item filterInPedestal = pedestal.getFilterInPedestal().getItem();
            if(filterInPedestal instanceof IPedestalFilter)
            {
                returner = ((IPedestalFilter) filterInPedestal).canAcceptItem(pedestal,stackIn,0);
            }

        }

        return returner;
    }

    public boolean passesFluidFilter(BasePedestalBlockEntity pedestal, ItemStack stackIn)
    {
        boolean returner = true;

        if(pedestal.hasFilter())
        {
            Item filterInPedestal = pedestal.getFilterInPedestal().getItem();
            if(filterInPedestal instanceof IPedestalFilter)
            {
                returner = ((IPedestalFilter) filterInPedestal).canAcceptItem(pedestal,stackIn,1);
            }

        }
        else
        {
            BucketItem bucket = ((BucketItem)stackIn.getItem());
            Fluid bucketFluid = bucket.getFluid();
            FluidStack fluidInTank = new FluidStack(bucketFluid,1000);
            return pedestal.canAcceptFluid(fluidInTank);
        }

        return returner;
    }

    public int getCountItemFilter(BasePedestalBlockEntity pedestal, ItemStack stackIn)
    {
        if(pedestal.hasFilter())
        {
            Item filterInPedestal = pedestal.getFilterInPedestal().getItem();
            if(filterInPedestal instanceof IPedestalFilter)
            {
                return ((IPedestalFilter) filterInPedestal).canAcceptCount(pedestal,stackIn,0);
            }

        }

        return stackIn.getCount();
    }














    /*public void upgradeActionMagnet(PedestalTileEntity pedestal, World world, List<ItemEntity> itemList, ItemStack itemInPedestal, BlockPos posOfPedestal)
    {
        ItemStack coinInPedestal = pedestal.getCoinOnPedestal();
        if(itemList.size()>0)
        {
            for(ItemEntity getItemFromList : itemList)
            {
                ItemStack copyStack = getItemFromList.getItem().copy();
                int maxSize = copyStack.getMaxStackSize();
                boolean stacksMatch = doItemsMatch(itemInPedestal,copyStack);
                if ((itemInPedestal.isEmpty() || stacksMatch ) && canThisPedestalReceiveItemStack(pedestal,world,posOfPedestal,copyStack))
                {
                    int spaceInPed = itemInPedestal.getMaxStackSize()-itemInPedestal.getCount();
                    if(stacksMatch)
                    {
                        if(spaceInPed > 0)
                        {
                            int itemInCount = getItemFromList.getItem().getCount();
                            int countToAdd = ( itemInCount<= spaceInPed)?(itemInCount):(spaceInPed);
                            getItemFromList.getItem().setCount(itemInCount-countToAdd);
                            copyStack.setCount(countToAdd);
                            pedestal.addItem(copyStack);
                            if(!pedestal.hasMuffler())world.playSound((PlayerEntity) null, posOfPedestal.getX(), posOfPedestal.getY(), posOfPedestal.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.0F);
                        }
                        else if(!hasAdvancedInventoryTargetingTwo(coinInPedestal))break;
                    }
                    else if(copyStack.getCount() <=maxSize)
                    {
                        getItemFromList.setItem(ItemStack.EMPTY);
                        getItemFromList.remove();
                        pedestal.addItem(copyStack);
                        if(!pedestal.hasMuffler())world.playSound((PlayerEntity) null, posOfPedestal.getX(), posOfPedestal.getY(), posOfPedestal.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.0F);

                    }
                    else
                    {
                        //If an ItemStackEntity has more than 64, we subtract 64 and inset 64 into the pedestal
                        int count = getItemFromList.getItem().getCount();
                        getItemFromList.getItem().setCount(count-maxSize);
                        copyStack.setCount(maxSize);
                        pedestal.addItem(copyStack);
                        if(!pedestal.hasMuffler())world.playSound((PlayerEntity) null, posOfPedestal.getX(), posOfPedestal.getY(), posOfPedestal.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, 1.0F);
                    }
                    if(!hasAdvancedInventoryTargetingTwo(coinInPedestal))break;
                }
            }
        }
    }*/













    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);

        if(p_41421_.getItem().equals(DeferredRegisterItems.PEDESTAL_UPGRADE_BASE.get()))
        {
            MutableComponent base = Component.translatable(getDescriptionId() + ".base_description");
            base.withStyle(ChatFormatting.DARK_RED);
            p_41423_.add(base);
        }

        if(p_41421_.getItem() instanceof IHasModeTypes)
        {
            //Display Current Mode
            int mode = getUpgradeMode(p_41421_);
            MutableComponent changed = Component.translatable(MODID + ".upgrade_tooltip_mode");
            ChatFormatting colorChange = ChatFormatting.GOLD;
            String typeString = "";
            switch(mode)
            {
                case 0: typeString = ".mode_tooltip_items"; break;
                case 1: typeString = ".mode_tooltip_fluids"; break;
                case 2: typeString = ".mode_tooltip_energy"; break;
                case 3: typeString = ".mode_tooltip_experience"; break;
                case 4: typeString = ".mode_tooltip_dust"; break;
                default: typeString = ".tooltip_error"; break;
            }
            changed.withStyle(colorChange);
            MutableComponent type = Component.translatable(MODID + typeString);
            changed.append(type);
            p_41423_.add(changed);

            //Separator
            MutableComponent separator = Component.translatable(MODID + ".tooltip_separator");
            p_41423_.add(separator);

            // List all the current modes and their status
            for(int i=0;i<5;i++)
            {
                MutableComponent modeIterator = getUpgradeModeComponentFromInt(i);
                MutableComponent typeIterator = (getTransportModeFromNBT(p_41421_,i))?(Component.translatable(MODID + ".upgrade_tooltip_type_enabled")):(Component.translatable(MODID + ".upgrade_tooltip_type_disabled"));
                modeIterator.append(Component.translatable(MODID + ".upgrade_tooltip_separator"));
                modeIterator.append(typeIterator);
                p_41423_.add(modeIterator);
            }

        }

        //Add a new Line if both are present
        if(p_41421_.getItem() instanceof ISelectableArea && p_41421_.getItem() instanceof IHasModeTypes)p_41423_.add(Component.literal(""));

        if(p_41421_.getItem() instanceof ISelectableArea)
        {
            if(hasTwoPointsSelected(p_41421_))
            {
                MutableComponent posTitle = Component.translatable(MODID + ".upgrade_tooltip_blockpos_title");
                posTitle.withStyle(ChatFormatting.GOLD);
                p_41423_.add(posTitle);

                //Separator
                MutableComponent separator = Component.translatable(MODID + ".tooltip_separator");
                p_41423_.add(separator);

                MutableComponent posOne = Component.translatable(MODID + ".upgrade_tooltip_blockpos_one");
                BlockPos blockPosOne = readBlockPosFromNBT(p_41421_,1);
                MutableComponent posOnePos = Component.literal(blockPosOne.getX() + "x " + blockPosOne.getY() + "y " + blockPosOne.getZ()+ "z");
                posOnePos.withStyle(ChatFormatting.GRAY);
                posOne.append(Component.translatable(MODID + ".upgrade_tooltip_separator"));
                posOne.append(posOnePos);

                p_41423_.add(posOne);

                MutableComponent posTwo = Component.translatable(MODID + ".upgrade_tooltip_blockpos_two");
                BlockPos blockPosTwo = readBlockPosFromNBT(p_41421_,2);
                MutableComponent posTwoPos = Component.literal(blockPosTwo.getX() + "x " + blockPosTwo.getY() + "y " + blockPosTwo.getZ()+ "z");
                posTwoPos.withStyle(ChatFormatting.GRAY);
                posTwo.append(Component.translatable(MODID + ".upgrade_tooltip_separator"));
                posTwo.append(posTwoPos);
                p_41423_.add(posTwo);
            }
        }

    }



    /*============================================================================
    ==============================================================================
    =========================    FAKE PLAYER  START    ===========================
    ==============================================================================
    ============================================================================*/

    /*public WeakReference<FakePlayer> fakePedestalPlayer(BasePedestalBlockEntity pedestal)
    {
        Level world = pedestal.getLevel();
        ItemStack upgrade = pedestal.getCoinOnPedestal();
        if(world instanceof ServerLevel slevel)
        {
            return new WeakReference<FakePlayer>(new MowLibFakePlayer(slevel , OwnerUtil.getPlayerFromStack(upgrade), OwnerUtil.getPlayerNameFromStack(upgrade)));
        }
        else return null;
    }

    public WeakReference<FakePlayer> fakePedestalPlayer(BasePedestalBlockEntity pedestal, ItemStack itemInHand)
    {
        Level world = pedestal.getLevel();
        ItemStack upgrade = pedestal.getCoinOnPedestal();
        if(world instanceof ServerLevel slevel)
        {
            return new WeakReference<FakePlayer>(new PedestalFakePlayer(slevel,OwnerUtil.getPlayerFromStack(upgrade), OwnerUtil.getPlayerNameFromStack(upgrade),pedestal.getPos(),itemInHand));
        }
        else return null;
    }*/

    /*============================================================================
    ==============================================================================
    =========================     FAKE PLAYER  END     ===========================
    ==============================================================================
    ============================================================================*/


}
