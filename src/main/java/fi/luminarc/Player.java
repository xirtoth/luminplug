package fi.luminarc;

public class Player {
    private String name;
    private int health;
    private int experience;
    private double money;
    private double bank;

    public Player(int health, String name) {
        this.health = health;
        this.name = name;

    }

    @Override
    public String toString() {
        return "Player: " + name + " Health: " + health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public int getExperience() {
        return experience;
    }

    public double getMoney() {
        return money;
    }

  public double getBank() {
    return bank;
  }
  public void setMoney(double money) {
    this.money = money;
  }
    public void setBank(double bank) {
        this.bank = bank;
    }
}