package com.mowmaster.pedestals.particles;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.IParticleData;

import java.util.LinkedList;

public class ParticleBase {
    public boolean isDone;
    int ticks;
    LinkedList<ParticleElem> particles;

    ClientWorld world;
    public ParticleBase(){
        isDone = false;
        ticks = 0;
        particles = new LinkedList<ParticleElem>();
        world = null;
    }

    public void tick(){
        ticks++;
    }

    public static double getCenterOfBlock(double a){
        return (a + .5);
    }

    public static class ParticleElem{
        public IParticleData particleData;
        public double x;
        public double y;
        public double z;
        public double xSpeed;
        public double ySpeed;
        public double zSpeed;

        public ParticleElem(IParticleData particleData, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            this.particleData = particleData;
            this.x = x;
            this.y = y;
            this.z = z;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            this.zSpeed = zSpeed;
        }

        public void addToWorld(ClientWorld world){
            world.addParticle(particleData, x, y, z, xSpeed,ySpeed,zSpeed);
        }
    }
}
