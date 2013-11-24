package org.test.fst;

import org.apache.lucene.store.DataInput;
import org.apache.lucene.store.DataOutput;
import org.apache.lucene.util.fst.Outputs;

import java.io.IOException;

public final class IntegerOffsetOutputs extends Outputs<Integer> {

    private final static Integer NO_OUTPUT = Integer.MIN_VALUE;

    private final static IntegerOffsetOutputs singleton = new IntegerOffsetOutputs();

    private IntegerOffsetOutputs() {
    }

    public static IntegerOffsetOutputs getSingleton() {
        return singleton;
    }

    @Override
    public Integer common(Integer output1, Integer output2) {
        return NO_OUTPUT;
    }

    @Override
    public Integer subtract(Integer output, Integer inc) {
        if (inc == NO_OUTPUT) {
            return output;
        } else if (output.equals(inc)) {
            return NO_OUTPUT;
        } else {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    @Override
    public Integer add(Integer prefix, Integer output) {
        if (prefix == NO_OUTPUT) {
            return output;
        } else if (output == NO_OUTPUT) {
            return prefix;
        } else {
            throw new UnsupportedOperationException("Not supported");
        }
    }

    @Override
    public Integer merge(Integer first, Integer second) {
        if (first == NO_OUTPUT) {
            return second;
        } else if (second == NO_OUTPUT) {
            return first;
        } else {
            return Math.min(first, second);
        }
    }

    @Override
    public void write(Integer output, DataOutput out) throws IOException {
        out.writeVInt(output);
    }

    @Override
    public Integer read(DataInput in) throws IOException {
        int v = in.readVInt();
        if (v == NO_OUTPUT) {
            return NO_OUTPUT;
        } else {
            return v;
        }
    }

    @Override
    public Integer getNoOutput() {
        return NO_OUTPUT;
    }

    @Override
    public String outputToString(Integer output) {
        return output.toString();
    }

    @Override
    public String toString() {
        return "IntegerOffsetOutputs";
    }

}
