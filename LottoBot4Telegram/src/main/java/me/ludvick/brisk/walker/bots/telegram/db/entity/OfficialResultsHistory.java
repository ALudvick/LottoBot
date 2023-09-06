package me.ludvick.brisk.walker.bots.telegram.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Date;

@Entity
@Table(name="Pais_History1")
public class OfficialResultsHistory {
    @Id
    private int id;
    @Column(name = "Lotto_Date")
    private java.sql.Date lottoDate;
    @Column(name = "Strong_Number")
    private int strongNumber;
    @Column(name = "Lotto_Numbers")
    private int[] lottoNumbers;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getLottoDate() {
        return lottoDate;
    }

    public void setLottoDate(Date lottoDate) {
        this.lottoDate = lottoDate;
    }

    public int getStrongNumber() {
        return strongNumber;
    }

    public void setStrongNumber(int strongNumber) {
        this.strongNumber = strongNumber;
    }

    public int[] getLottoNumbers() {
        return lottoNumbers;
    }

    public void setLottoNumbers(int[] lottoNumbers) {
        this.lottoNumbers = lottoNumbers;
    }
}
