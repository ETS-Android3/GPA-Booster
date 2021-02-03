package com.example.maimyou.Classes;

public class ActionListener {
    Action action;
    public void performAction() {
        if(action!=null){
            action.action();
        }
    }
    public interface Action{
         void action();
    }
    public void setOnActionPerformed(Action action){
        this.action=action;
    }
}
