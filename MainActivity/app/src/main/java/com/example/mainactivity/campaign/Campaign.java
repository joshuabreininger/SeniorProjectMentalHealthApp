package com.example.mainactivity.campaign;

import android.content.Context;

import com.example.mainactivity.Data.DataBaseHandler;

public class Campaign {

    private final int PHYSICAL_ATTACK = 0;
    private final int MAGIC_ATTACK = 1;

    private Player player;
    private Enemy enemy;

    public Campaign(Player pl, Enemy en){
        player = pl;
        enemy = en;
    }

    public void newEnemy(Enemy temp){ enemy = temp; }

    public Player getPlayerStats(){
        return player;
    }

    public Enemy getEnemyStats(){
        return enemy;
    }

    public double attack(int type){
        switch (type) {
            case 0:
                dealDamage(player.attackDamage + player.damgeMod - enemy.physicalResistance);
                return enemy.enemyHealth;
            case 1:
                if(player.mana == 100) {
                    dealDamage(player.magicDamage + player.damgeMod - enemy.magicResistance);
                    player.mana = 0;
                    return enemy.enemyHealth;
                }
        }
        return -1.0;
    }

    private void dealDamage(int damage){
        enemy.enemyHealth -= damage;
        if (enemy.enemyHealth <= 0) {
            enemy.enemyHealth = 0;
            gainExp();
        }
        useTurn();
    }
    
    private void gainExp(){ player.exp += 35; }

    private void useTurn(){
        player.numberOfTurns--;
    }

    public boolean isEnemyGone(){
        return enemy == null ||enemy.enemyHealth == 0.0;
    }

    public boolean canAttack(){
        return player.numberOfTurns > 0;
    }

    public int getEnemyHealth(){
        return enemy.enemyHealth;
    }

    public int getEnemyMaxHealth(){
        return enemy.enemyMaxHealth;
    }

    public int getEnemyImageID(){ return enemy.imageID; }

    public int getAttackDamage() { return player.attackDamage; }

    public int getPlayerMana(){ return player.mana; }

    public int getPlayerMaxMana(){ return player.maximumMana; }

    public int getPlayerExp(){ return player.exp; }
}