package org.boon.template.support;

/**
 * Created by Richard on 9/17/14.
 */
public class LoopTagStatus {
    int count;
    Object current;
    Integer begin;
    Integer end;
    Integer step;
    int index;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getCurrent() {
        return current;
    }

    public void setCurrent(Object current) {
        this.current = current;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public boolean isFirst() {
        boolean first = false;

        if (begin==null && index==0) {
            first=true;
        } else if (begin!=null && index==begin) {
            first = true;
        }
        return first;

    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isLast() {


        boolean last = false;

        if (end==null && index+1==count) {
            last=true;
        } else if (end!=null && index+1==end) {
            last = true;
        }
        return last;
    }

    public void setBegin(Integer begin) {
        this.begin = begin;
    }
}
