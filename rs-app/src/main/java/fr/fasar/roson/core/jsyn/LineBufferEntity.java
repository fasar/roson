package fr.fasar.roson.core.jsyn;

public class LineBufferEntity {

    double[][] inputs;

    public LineBufferEntity(int nbInput) {
        inputs = new double[nbInput][];
    }

    public double[] getInput(int numPart) {
        return inputs[numPart];
    }

    public void setInput(int numPart, double[] input) {
        this.inputs[numPart] = input;
    }
}
