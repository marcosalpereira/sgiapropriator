package br.com.marcosoft.sgi.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * An Activity.
 */
public class TaskDailySummary {

    private Date data;

    private Task task;

    private int sum;

    private boolean apropriado;
    
    private List<String> numerosLinhas = new ArrayList<String>();

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

    public int getSum() {
        return sum;
    }

    public void setSum(int duracao) {
        this.sum = duracao;
    }



    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TaskDailySummary) {
            TaskDailySummary that = (TaskDailySummary) obj;
            return new EqualsBuilder()
                .append(this.getData(), that.getData())
                .append(this.getTask(), that.getTask())
                .isEquals();
        }

        return false;
    }

    /**{@inheritDoc}*/
    @Override
    public int hashCode() {
        return new HashCodeBuilder(123, 1235)
            .append(this.getData())
            .append(this.getTask())
            .toHashCode();
    }

    @Override
    public String toString() {
        return this.task + "," + this.data + "," + this.sum;
    }

    public void setApropriado(boolean apropriado) {
        this.apropriado = apropriado;
    }
    
    public boolean isApropriado() {
        return apropriado;
    }
    
    public List<String> getNumerosLinhas() {
        return numerosLinhas;
    }

}
