package me.ludvick.brisk.walker.bots.telegram.db.entity;

import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;

public class LottoGame {

    private String lotto_uuid;
    private int lottoId;
    private java.sql.Date lottoDate;
    private int lottoStrongNumber;
    private Integer[] lottoRegularNumbers;

    public LottoGame() {}

    public LottoGame(String lotto_uuid, int lottoId, Date lottoDate, int lottoStrongNumber, Integer[] lottoRegularNumbers) {
        this.lotto_uuid = lotto_uuid;
        this.lottoId = lottoId;
        this.lottoDate = lottoDate;
        this.lottoStrongNumber = lottoStrongNumber;
        this.lottoRegularNumbers = lottoRegularNumbers;
    }

    public String getLotto_uuid() {
        return lotto_uuid;
    }

    public void setLotto_uuid(String lotto_uuid) {
        this.lotto_uuid = lotto_uuid;
    }

    public int getLottoId() {
        return lottoId;
    }

    public void setLottoId(int lottoId) {
        this.lottoId = lottoId;
    }

    public Date getLottoDate() {
        return lottoDate;
    }

    public void setLottoDate(Date lottoDate) {
        this.lottoDate = lottoDate;
    }

    public int getLottoStrongNumber() {
        return lottoStrongNumber;
    }

    public void setLottoStrongNumber(int lottoStrongNumber) {
        this.lottoStrongNumber = lottoStrongNumber;
    }

    public Integer[] getLottoRegularNumbers() {
        return lottoRegularNumbers;
    }

    public void setLottoRegularNumbers(Integer[] lottoRegularNumbers) {
        this.lottoRegularNumbers = lottoRegularNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LottoGame lottoGame = (LottoGame) o;
        return lottoId == lottoGame.lottoId && lottoStrongNumber == lottoGame.lottoStrongNumber && Objects.equals(lotto_uuid, lottoGame.lotto_uuid) && Objects.equals(lottoDate, lottoGame.lottoDate) && Arrays.equals(lottoRegularNumbers, lottoGame.lottoRegularNumbers);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(lotto_uuid, lottoId, lottoDate, lottoStrongNumber);
        result = 31 * result + Arrays.hashCode(lottoRegularNumbers);
        return result;
    }

    @Override
    public String toString() {
        return "LottoGame{" +
                "lotto_uuid='" + lotto_uuid + '\'' +
                ", lottoId=" + lottoId +
                ", lottoDate=" + lottoDate +
                ", lottoStrongNumber=" + lottoStrongNumber +
                ", lottoRegularNumbers=" + Arrays.toString(lottoRegularNumbers) +
                '}';
    }
}
