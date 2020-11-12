package work.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class GoodModel {
    private String goodId;
    private int goodCount;

    public GoodModel() {

    }

    public GoodModel(String goodId, int goodCount) {
        this.goodId = goodId;
        this.goodCount = goodCount;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public String getGoodId() {
        return goodId;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public void setGoodId(String goodId) {
        this.goodId = goodId;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.goodId).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        GoodModel gd = (GoodModel) obj;
        return new EqualsBuilder().append(goodId, gd.goodId).isEquals();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return "[ goodId :" + goodId + " ,goodCount :" + goodCount + "]";
    }
}
