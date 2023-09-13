package me.ludvick.brisk.walker.bots.telegram.db.entity;

import java.sql.Date;
import java.util.Objects;

public class LottoGame {

    private int id;
    private java.sql.Date lottoDate;
    private int strongNumber;
    private int lottoNumber1;
    private int lottoNumber2;
    private int lottoNumber3;
    private int lottoNumber4;
    private int lottoNumber5;
    private int lottoNumber6;

    public LottoGame() {}

    public LottoGame(
            int id,
            Date lottoDate,
            int strongNumber,
            int lottoNumber1,
            int lottoNumber2,
            int lottoNumber3,
            int lottoNumber4,
            int lottoNumber5,
            int lottoNumber6) {
        this.id = id;
        this.lottoDate = lottoDate;
        this.strongNumber = strongNumber;
        this.lottoNumber1 = lottoNumber1;
        this.lottoNumber2 = lottoNumber2;
        this.lottoNumber3 = lottoNumber3;
        this.lottoNumber4 = lottoNumber4;
        this.lottoNumber5 = lottoNumber5;
        this.lottoNumber6 = lottoNumber6;
    }

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

    public int getLottoNumber1() {
        return lottoNumber1;
    }

    public void setLottoNumber1(int lottoNumber1) {
        this.lottoNumber1 = lottoNumber1;
    }

    public int getLottoNumber2() {
        return lottoNumber2;
    }

    public void setLottoNumber2(int lottoNumber2) {
        this.lottoNumber2 = lottoNumber2;
    }

    public int getLottoNumber3() {
        return lottoNumber3;
    }

    public void setLottoNumber3(int lottoNumber3) {
        this.lottoNumber3 = lottoNumber3;
    }

    public int getLottoNumber4() {
        return lottoNumber4;
    }

    public void setLottoNumber4(int lottoNumber4) {
        this.lottoNumber4 = lottoNumber4;
    }

    public int getLottoNumber5() {
        return lottoNumber5;
    }

    public void setLottoNumber5(int lottoNumber5) {
        this.lottoNumber5 = lottoNumber5;
    }

    public int getLottoNumber6() {
        return lottoNumber6;
    }

    public void setLottoNumber6(int lottoNumber6) {
        this.lottoNumber6 = lottoNumber6;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LottoGame lottoGame = (LottoGame) o;
        return id == lottoGame.id && strongNumber == lottoGame.strongNumber && lottoNumber1 == lottoGame.lottoNumber1 && lottoNumber2 == lottoGame.lottoNumber2 && lottoNumber3 == lottoGame.lottoNumber3 && lottoNumber4 == lottoGame.lottoNumber4 && lottoNumber5 == lottoGame.lottoNumber5 && lottoNumber6 == lottoGame.lottoNumber6 && Objects.equals(lottoDate, lottoGame.lottoDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, lottoDate, strongNumber, lottoNumber1, lottoNumber2, lottoNumber3, lottoNumber4, lottoNumber5, lottoNumber6);
    }

    @Override
    public String toString() {
        return "LottoGame{" +
                "id=" + id +
                ", lottoDate=" + lottoDate +
                ", strongNumber=" + strongNumber +
                ", lottoNumber1=" + lottoNumber1 +
                ", lottoNumber2=" + lottoNumber2 +
                ", lottoNumber3=" + lottoNumber3 +
                ", lottoNumber4=" + lottoNumber4 +
                ", lottoNumber5=" + lottoNumber5 +
                ", lottoNumber6=" + lottoNumber6 +
                '}';
    }
}
