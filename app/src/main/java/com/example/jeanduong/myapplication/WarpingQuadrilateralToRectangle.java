package com.example.jeanduong.myapplication;

/**
 * Created by jeanduong on 09/11/2016.
 */

public class WarpingQuadrilateralToRectangle {

    SquareMatrix TransformationMatrix;

    public WarpingQuadrilateralToRectangle(int X_1, int Y_1, int X_2, int Y_2, int X_3, int Y_3, int X_4, int Y_4,
                                           int x_1, int y_1, int x_2, int y_2, int x_3, int y_3, int x_4, int y_4) {
        SquareMatrix ModelMatrix = new SquareMatrix(8);

        ModelMatrix.data[0][0] = x_1;
        ModelMatrix.data[2][0] = x_2;
        ModelMatrix.data[4][0] = x_3;
        ModelMatrix.data[6][0] = x_4;

        ModelMatrix.data[0][1] = y_1;
        ModelMatrix.data[2][1] = y_2;
        ModelMatrix.data[4][1] = y_3;
        ModelMatrix.data[6][1] = y_4;

        ModelMatrix.data[0][2] = 1.0;
        ModelMatrix.data[2][2] = 1.0;
        ModelMatrix.data[4][2] = 1.0;
        ModelMatrix.data[6][2] = 1.0;

        ModelMatrix.data[1][3] = x_1;
        ModelMatrix.data[3][3] = x_2;
        ModelMatrix.data[5][3] = x_3;
        ModelMatrix.data[7][3] = x_4;

        ModelMatrix.data[1][4] = y_1;
        ModelMatrix.data[3][4] = y_2;
        ModelMatrix.data[5][4] = y_3;
        ModelMatrix.data[7][4] = y_4;

        ModelMatrix.data[1][5] = 1.0;
        ModelMatrix.data[3][5] = 1.0;
        ModelMatrix.data[5][5] = 1.0;
        ModelMatrix.data[7][5] = 1.0;

        ModelMatrix.data[0][6] = -X_1 * x_1;
        ModelMatrix.data[1][6] = -Y_1 * x_1;
        ModelMatrix.data[2][6] = -X_2 * x_2;
        ModelMatrix.data[3][6] = -Y_2 * x_2;
        ModelMatrix.data[4][6] = -X_3 * x_3;
        ModelMatrix.data[5][6] = -Y_3 * x_3;
        ModelMatrix.data[6][6] = -X_4 * x_4;
        ModelMatrix.data[7][6] = -Y_4 * x_4;

        ModelMatrix.data[0][7] = -X_1 * y_1;
        ModelMatrix.data[1][7] = -Y_1 * y_1;
        ModelMatrix.data[2][7] = -X_2 * y_2;
        ModelMatrix.data[3][7] = -Y_2 * y_2;
        ModelMatrix.data[4][7] = -X_3 * y_3;
        ModelMatrix.data[5][7] = -Y_3 * y_3;
        ModelMatrix.data[6][7] = -X_4 * y_4;
        ModelMatrix.data[7][7] = -Y_4 * y_4;

        double det_M = ModelMatrix.Determinant();

        // Cramer solution

        double[] column_backup = new double[8];
        double[] solutions = new double[8];

        for (int c = 0; c < 8; ++c)
        {
            column_backup[0] = ModelMatrix.data[0][c];
            column_backup[1] = ModelMatrix.data[1][c];
            column_backup[2] = ModelMatrix.data[2][c];
            column_backup[3] = ModelMatrix.data[3][c];
            column_backup[4] = ModelMatrix.data[4][c];
            column_backup[5] = ModelMatrix.data[5][c];
            column_backup[6] = ModelMatrix.data[6][c];
            column_backup[7] = ModelMatrix.data[7][c];

            ModelMatrix.data[0][c] = X_1;
            ModelMatrix.data[1][c] = Y_1;
            ModelMatrix.data[2][c] = X_2;
            ModelMatrix.data[3][c] = Y_2;
            ModelMatrix.data[4][c] = X_3;
            ModelMatrix.data[5][c] = Y_3;
            ModelMatrix.data[6][c] = X_4;
            ModelMatrix.data[7][c] = Y_4;

            solutions[c] = ModelMatrix.Determinant() / det_M;

            ModelMatrix.data[0][c] = column_backup[0];
            ModelMatrix.data[1][c] = column_backup[1];
            ModelMatrix.data[2][c] = column_backup[2];
            ModelMatrix.data[3][c] = column_backup[3];
            ModelMatrix.data[4][c] = column_backup[4];
            ModelMatrix.data[5][c] = column_backup[5];
            ModelMatrix.data[6][c] = column_backup[6];
            ModelMatrix.data[7][c] = column_backup[7];
        }

        TransformationMatrix = new SquareMatrix(3);

        TransformationMatrix.data[0][0] = solutions[0];
        TransformationMatrix.data[1][0] = solutions[1];
        TransformationMatrix.data[2][0] = solutions[2];
        TransformationMatrix.data[0][1] = solutions[3];
        TransformationMatrix.data[1][1] = solutions[4];
        TransformationMatrix.data[2][1] = solutions[5];
        TransformationMatrix.data[0][2] = solutions[6];
        TransformationMatrix.data[1][2] = solutions[7];
        TransformationMatrix.data[2][2] = 1.0;

        SquareMatrix InverseTransformatiomMatrix = new SquareMatrix(3);

        // Inversion for 3x3 matrix (for general case, see cofactors or LU decomposition, etc.)
        // Simplification due to TransformationMatrix.data[2][2] = 1.0

        InverseTransformatiomMatrix.data[0][0] = TransformationMatrix.data[1][1] - TransformationMatrix.data[1][2] * TransformationMatrix.data[2][1];
        InverseTransformatiomMatrix.data[0][1] = TransformationMatrix.data[0][2] * TransformationMatrix.data[2][1] - TransformationMatrix.data[0][1];
        InverseTransformatiomMatrix.data[0][2] = TransformationMatrix.data[0][1] * TransformationMatrix.data[1][2] - TransformationMatrix.data[0][2] * TransformationMatrix.data[1][1];
        InverseTransformatiomMatrix.data[1][0] = TransformationMatrix.data[1][2] * TransformationMatrix.data[2][0] - TransformationMatrix.data[1][0];
        InverseTransformatiomMatrix.data[1][1] = TransformationMatrix.data[0][0] - TransformationMatrix.data[0][2] * TransformationMatrix.data[2][0];
        InverseTransformatiomMatrix.data[1][2] = TransformationMatrix.data[0][2] * TransformationMatrix.data[1][0] - TransformationMatrix.data[0][0] * TransformationMatrix.data[1][2];
        InverseTransformatiomMatrix.data[2][0] = TransformationMatrix.data[1][0] * TransformationMatrix.data[2][1] - TransformationMatrix.data[1][1] * TransformationMatrix.data[2][0];
        InverseTransformatiomMatrix.data[2][1] = TransformationMatrix.data[0][1] * TransformationMatrix.data[2][0] - TransformationMatrix.data[0][0] * TransformationMatrix.data[2][1];
        InverseTransformatiomMatrix.data[2][2] = TransformationMatrix.data[0][0] * TransformationMatrix.data[1][1] - TransformationMatrix.data[0][1] * TransformationMatrix.data[1][0];

        InverseTransformatiomMatrix.ScaleCells(1.0 / TransformationMatrix.Determinant());
    }

}
