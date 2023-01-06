package Entities;

import javax.persistence.*;

@Entity
@Table(name = "debt", schema = "public", catalog = "Debts")
public class DebtEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "debt_id", nullable = false)
    private int debtId;
    @Basic
    @Column(name = "receiver", nullable = false, length = -1)
    private String receiver;
    @Basic
    @Column(name = "debtor", nullable = false, length = -1)
    private String debtor;
    @Basic
    @Column(name = "sum", nullable = false)
    private int sum;

    public DebtEntity() {}

    public DebtEntity(String receiver, String debtor, int sum) {
        this.receiver = receiver;
        this.debtor = debtor;
        this.sum = sum;
    }

    public int getDebtId() {
        return debtId;
    }

    public void setDebtId(int debtId) {
        this.debtId = debtId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getDebtor() {
        return debtor;
    }

    public void setDebtor(String debtor) {
        this.debtor = debtor;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
}
