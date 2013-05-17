package br.com.marcosoft.sgi.model;

import java.util.Date;

/**
 * An Activity.
 */
public class TaskRecord {

    private Date data;

    private Task task;

    private int duracao;

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public int getDuracao() {
        return duracao;
    }

    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public String getSistema() {
        return task.getSistema();
    }


}
