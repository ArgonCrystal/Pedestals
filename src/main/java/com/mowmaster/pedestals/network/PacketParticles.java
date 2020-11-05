package com.mowmaster.pedestals.network;

import net.minecraft.block.EnchantingTableBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EnchantmentTableParticle;
import net.minecraft.client.renderer.entity.EnderCrystalRenderer;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraft.client.world.ClientWorld;

import java.util.Random;
import java.util.function.Supplier;

//To learn how this works and for testing purposes:
//https://github.com/baileyholl/Ars-Nouveau/blob/0cdb8fbb483ca0f945de26c633955cfb1c05c925/src/main/java/com/hollingsworth/arsnouveau/common/network/PacketANEffect.java#L17

public class PacketParticles
{
    private final EffectType type;
    private final double x;
    private final double y;
    private final double z;
    private final int[] args;

    public PacketParticles(EffectType type, double x, double y, double z, int... args) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.args = args;
    }

    public PacketParticles(EffectType type, BlockPos pos, int... args){
        this(type, pos.getX(), pos.getY(), pos.getZ(), args);
    }


    public static PacketParticles decode(PacketBuffer buf) {
        EffectType type = EffectType.values()[buf.readByte()];
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int[] args = new int[type.argCount];

        for (int i = 0; i < args.length; i++) {
            args[i] = buf.readVarInt();
        }
        return new PacketParticles(type, x, y, z, args);
    }

    public static void encode(PacketParticles msg, PacketBuffer buf) {
        buf.writeByte(msg.type.ordinal());
        buf.writeDouble(msg.x);
        buf.writeDouble(msg.y);
        buf.writeDouble(msg.z);

        for (int i = 0; i < msg.type.argCount; i++) {
            buf.writeVarInt(msg.args[i]);
        }
    }

    public static class Handler {
        public static void handle(final PacketParticles message, final Supplier<NetworkEvent.Context> ctx) {
            if (ctx.get().getDirection().getReceptionSide().isServer()) {
                ctx.get().setPacketHandled(true);
                return;
            }
            ctx.get().enqueueWork(new Runnable() {
                // Use anon - lambda causes classloading issues
                @Override
                public void run() {
                    Minecraft mc = Minecraft.getInstance();
                    ClientWorld world = mc.world;
                    switch (message.type){
                        case ANY_COLOR:{
                            for(int i =0; i < 10; i++){
                                double d0 = message.x +0.5; //+ world.rand.nextFloat();
                                double d1 = message.y +1.0;//+ world.rand.nextFloat() ;
                                double d2 = message.z +0.5 ; //+ world.rand.nextFloat();
                                float red = (float)message.args[0]/255;
                                float green = (float)message.args[1]/255;
                                float blue = (float)message.args[2]/255;
                                world.addParticle(new RedstoneParticleData(red,green,blue,1.0f),d0, d1, d2, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3, (world.rand.nextFloat() * 1 - 0.5)/3);
                                //new PacketParticles(EffectType.ANY_COLOR,posTarget.getX(),posTarget.getY()-0.5f,posTarget.getZ(),0.0f,1.0f,0.0f));
                            }
                            break;
                        }
                        case ANY_COLOR_BEAM:{
                            for(int z =0; z < 10; z++){
                                //args are destination pos xyz then pedestal xyz
                                double d0 = message.x +0.5; //+ world.rand.nextFloat();
                                double d1 = message.y +1.0;//+ world.rand.nextFloat() ;
                                double d2 = message.z +0.5 ; //+ world.rand.nextFloat();
                                double x2 = message.args[0];
                                double y2 = message.args[1];
                                double z2 = message.args[2];

                                //distance from pedestal to block being ticked
                                double x3 = x2 - d0;
                                double y3 = y2 - d1;
                                double z3 = z2 - d2;

                                //Currently particles go from the block being ticked(in grower) to the pedestal [this needs reversed somehow]
                                BlockPos pos = new BlockPos(x2,y2,z2);
                                Random rand = new Random();
                                world.addParticle(ParticleTypes.ENCHANT, (double)pos.getX() + 0.5D, (double)pos.getY() + 2.0D, (double)pos.getZ() + 0.5D, (double)((float)x3 + rand.nextFloat()) - 0.5D, (double)((float)y3 - rand.nextFloat() - 1.0F), (double)((float)z3 + rand.nextFloat()) - 0.5D);
                                //EnchantmentTableParticle
                            }
                            break;
                        }
                    }

                };
            });
            ctx.get().setPacketHandled(true);

        }
    }
    public enum EffectType {
        ANY_COLOR(3),
        ANY_COLOR_BEAM(3)
        ;

        private final int argCount;

        EffectType(int argCount) {
            this.argCount = argCount;
        }
    }
}
