package com.example.jeanduong.myapplication;

// This is a custom experimental version of optimized warping between a quadrilateral and
// a rectangle.
//
// Not tested yet!!!

class CustomQuadrilateralWarping {
    private int X_1, Y_1, X_2, Y_2, X_3, Y_3, X_4, Y_4; // Quadrilateral's vertices
    private int x_1, y_1, x_2, y_2, x_3, y_3, x_4, y_4; // Rectangle's vertices
    private double a, b, c, d, e, f, g, h;

    CustomQuadrilateralWarping(int XX_1, int YY_1, int XX_2, int YY_2, int XX_3, int YY_3, int XX_4, int YY_4,
                               int xx_1, int yy_1, int xx_2, int yy_2, int xx_3, int yy_3, int xx_4, int yy_4)
    {
        X_1 = XX_1; X_2 = XX_2; X_3 = XX_3; X_4 = XX_4;
        Y_1 = YY_1; Y_2 = YY_2; Y_3 = YY_3; Y_4 = YY_4;
        x_1 = xx_1; x_2 = xx_2; x_3 = xx_3; x_4 = xx_4;
        y_1 = yy_1; y_2 = yy_2; y_3 = yy_3; y_4 = yy_4;

        // Compute determinant for M
        double det_M = DeterminantMainMatrix(CreateMainMatrix());

        // Compute determinant for Ma and Mb

        double det_Ma = DeterminantMatrixMa(CreateMatrixMa());

        SquareMatrix M = CreateMatrixMb();

        M.SwapColumns(0, 1);

        double det_Mb = - DeterminantMatrixMa(M);

        // Compute determinant for Mc

        M = CreateMatrixMc();

        double det_Mc = DeterminantMatrixMc(M);

        // Compute determinant for Md and Me
        M = CreateMatrixMd();

        double det_Md = DeterminantMatrixMd(M);

        M = CreateMatrixMe();

        M.SwapColumns(3, 4);

        double det_Me = - DeterminantMatrixMd(M);

        // Compute determinant for Mf

        M = CreateMatrixMf();

        double det_Mf = DeterminantMatrixMf(M);

        // Compute determinant for Mg and Mh
        double det_Mg = DeterminantMainMatrix(CreateMatrixMg());
        double det_Mh = DeterminantMainMatrix(CreateMatrixMh());
    }

    private SquareMatrix CreateMainMatrix()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private double DeterminantMainMatrix(SquareMatrix M)
    {
        int nb_swaps = 0;

        nb_swaps += M.SwapRows(1, 6);
        nb_swaps += M.SwapRows(3, 4);
        nb_swaps += M.SwapColumnsWithin(0, 2, 0, 3);
        nb_swaps += M.SwapColumnsWithin(3, 5, 4, 7);

        M.CumulateRowWithin(1, 0, 1, 2, -1.0); M.CumulateRowWithin(1, 0, 6, 7, -1.0);
        M.CumulateRowWithin(2, 0, 1, 2, -1.0); M.CumulateRowWithin(2, 0, 6, 7, -1.0);
        M.CumulateRowWithin(3, 0, 1, 2, -1.0); M.CumulateRowWithin(3, 0, 6, 7, -1.0);
        M.NullifyColumnWithin(0, 1, 3);

        M.CumulateRowWithin(5, 4, 4, 7, -1.0);
        M.CumulateRowWithin(6, 4, 4, 7, -1.0);
        M.CumulateRowWithin(7, 4, 4, 7, -1.0);
        M.NullifyColumnWithin(3, 5, 7);

        if (M.data[1][1] == 0.0) // Search a non-zero pivot
        {
            if (M.data[2][1] != 0.0)
                nb_swaps += M.SwapRowsWithin(1, 2, 1, 2);
            else if (M.data[3][1] != 0.0)
            {
                nb_swaps += M.SwapRowsWithin(1, 3, 1, 2);
                M.SwapRowsWithin(1, 3, 6, 7);
            }
        }

        if (M.data[1][1] != 0.0) // If pivot found
        {
            if (M.data[2][1] != 0.0)
            {
                M.CumulateRowWithin(2, 1, 2, 2, -M.data[2][1] / M.data[1][1]);
                M.data[2][1] = 0.0;
            }
            if (M.data[3][1] != 0.0)
            {
                double weight = -M.data[3][1] / M.data[1][1];

                M.CumulateRowWithin(3, 1, 2, 2, weight);
                M.CumulateRowWithin(3, 1, 6, 7, weight);
                M.data[3][1] = 0.0;
            }
        }

        if (M.data[2][2] != 0.0)
        {
            if (M.data[3][2] != 0.0)
            {
                M.CumulateRowWithin(3, 2, 6, 7, - M.data[3][2] / M.data[2][2]);
                M.data[3][2] = 0.0;
            }
        }
        else if (M.data[3][2] != 0)
        {
            M.data[2][2] = M.data[3][2];
            nb_swaps += M.SwapRowsWithin(2, 3, 6, 7);
        }

        nb_swaps += M.SwapRows(3, 4);

        return Calculus.sgn(nb_swaps) * M.data[1][1] * M.data[2][2] * // from upper-left block
                M.MakeSquareBlock(4, 7, 4, 7).Determinant();
    }

    private SquareMatrix CreateMatrixMa()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = X_1;
        M.data[1][0] = Y_1;
        M.data[2][0] = X_2;
        M.data[3][0] = Y_2;
        M.data[4][0] = X_3;
        M.data[5][0] = Y_3;
        M.data[6][0] = X_4;
        M.data[7][0] = Y_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private double DeterminantMatrixMa(SquareMatrix M)
    {
        int nb_swaps = 0;

        nb_swaps += M.SwapColumns(0, 2);
        nb_swaps += M.SwapRows(1, 6);
        nb_swaps += M.SwapRows(3, 4);

        M.CumulateRowWithin(1, 0, 1, 2, -1.0); M.CumulateRowWithin(1, 0, 6, 7, -1.0);
        M.CumulateRowWithin(2, 0, 1, 2, -1.0); M.CumulateRowWithin(2, 0, 6, 7, -1.0);
        M.CumulateRowWithin(3, 0, 1, 2, -1.0); M.CumulateRowWithin(3, 0, 6, 7, -1.0);
        M.NullifyColumnWithin(0, 1, 3);

        if (M.data[1][1] == 0.0)
        {
            if (M.data[2][1] != 0.0)
                nb_swaps += M.SwapRows(1, 2);
            else if (M.data[3][1] != 0.0)
                nb_swaps += M.SwapRows(1, 3);
        }

        if (M.data[1][1] != 0.0)
        {
            if (M.data[2][1] != 0.0)
                M.CumulateRow(2, 1, - M.data[2][1] / M.data[1][1]);
            if (M.data[3][1] != 0.0)
                M.CumulateRow(3, 1, - M.data[3][1] / M.data[1][1]);
        }

        nb_swaps += M.SwapColumns(2, 5);

        nb_swaps += M.SwapRowsFrom(2, 7, 2);
        nb_swaps += M.SwapRowsFrom(3, 6, 2);

        M.CumulateRow(3, 2, -1.0);
        M.CumulateRow(4, 2, -1.0);
        M.CumulateRow(5, 2, -1.0);

        if (M.data[3][3] == 0.0)
        {
            if (M.data[4][3] != 0.0)
                nb_swaps += M.SwapRowsFrom(4, 3, 3);
            else if (M.data[5][3] != 0.0)
                nb_swaps += M.SwapRowsFrom(5, 3, 3);
        }

        if (M.data[3][3] != 0.0)
        {
            if (M.data[4][3] != 0.0)
            {
                M.CumulateRowFrom(4, 3, 4, -M.data[4][3] / M.data[3][3]);
                //M.data[4][3] = 0.0;
            }
            if (M.data[5][3] != 0.0)
            {
                M.CumulateRowFrom(5, 3, 4, -M.data[5][3] / M.data[3][3]);
                //M.data[5][3] = 0.0;
            }
        }

        if (M.data[4][4] != 0.0)
        {
            if (M.data[5][4] != 0.0)
            {
                M.CumulateRowFrom(5, 4, 5, -M.data[5][4] / M.data[4][4]);
                //M.data[5][4] = 0.0;
            }
        }
        else if (M.data[5][4] != 0.0)
            nb_swaps += M.SwapRowsFrom(4, 5, 5);

        if (M.data[5][5] == 0.0)
        {
            if (M.data[6][5] != 0.0)
                nb_swaps += M.SwapRowsFrom(5, 6, 6);
            else if (M.data[7][7] != 0.0)
                nb_swaps += M.SwapRowsFrom(5, 7, 6);
        }

        if (M.data[5][5] != 0.0)
        {
            if (M.data[6][5] != 0.0)
            {
                M.CumulateRowFrom(6, 5, 6, - M.data[6][5] / M.data[5][5]);
                //M.data[6][5] = 0.0;
            }
            if (M.data[7][5] != 0.0)
            {
                M.CumulateRowFrom(7, 5, 6, - M.data[7][5] / M.data[5][5]);
                //M.data[7][5] = 0.0;
            }
        }

        if (M.data[6][6] != 0.0)
        {
            if (M.data[7][6] != 0.0)
            {
                M.CumulateRowFrom(7, 6, 7, - M.data[7][6] / M.data[6][6]);
                //M.data[7][6] = 0.0;
            }
        }
        else if (M.data[7][6] != 0.0)
            nb_swaps += M.SwapRowsFrom(6, 7, 6);

        return Calculus.sgn(nb_swaps) * M.DiagonalProduct();
    }

    private SquareMatrix CreateMatrixMb()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = X_1;
        M.data[1][1] = Y_1;
        M.data[2][1] = X_2;
        M.data[3][1] = Y_2;
        M.data[4][1] = X_3;
        M.data[5][1] = Y_3;
        M.data[6][1] = X_4;
        M.data[7][1] = Y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private SquareMatrix CreateMatrixMc()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = X_1;
        M.data[1][2] = Y_1;
        M.data[2][2] = X_2;
        M.data[3][2] = Y_2;
        M.data[4][2] = X_3;
        M.data[5][2] = Y_3;
        M.data[6][2] = X_4;
        M.data[7][2] = Y_4;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private double DeterminantMatrixMc(SquareMatrix M)
    {
        int nb_swaps = 0;

        if (M.data[0][0] == 0.0)
        {
            if (M.data[2][0] != 0.0)
                nb_swaps += M.SwapRows(0, 2);
            else if (M.data[4][0] != 0.0)
                nb_swaps += M.SwapRows(0, 4);
            else if (M.data[6][0] != 0.0)
                nb_swaps += M.SwapRows(0, 6);
        }

        if (M.data[0][0] != 0.0)
        {
            if (M.data[2][0] != 0.0)
                M.CumulateRow(2, 0, - M.data[2][0] / M.data[0][0]);
            if (M.data[4][0] != 0.0)
                M.CumulateRow(4, 0, - M.data[4][0] / M.data[0][0]);
            if (M.data[6][0] != 0.0)
                M.CumulateRow(6, 0, - M.data[6][0] / M.data[0][0]);
        }

        nb_swaps += M.SwapColumns(1, 5);

        M.CumulateRow(3, 1, -1.0);
        M.CumulateRow(5, 1, -1.0);
        M.CumulateRow(7, 1, -1.0);

        nb_swaps += M.SwapColumns(2, 5);

        if (M.data[2][2] == 0.0)
        {
            if (M.data[4][2] != 0.0)
            {
                nb_swaps += M.SwapRowsFrom(2, 4, 5);
                M.data[2][2] = M.data[4][2];
                M.data[4][2] = 0.0;
            }
            else if (M.data[6][2] != 0.0)
            {
                nb_swaps += M.SwapRowsFrom(2, 6, 5);
                M.data[2][2] = M.data[6][2];
                M.data[6][2] = 0.0;
            }
        }

        if (M.data[2][2] != 0.0)
        {
            if (M.data[4][2] != 0.0)
            {
                M.CumulateRowFrom(4, 2, 5, - M.data[4][2] / M.data[2][2]);
                M.data[4][2] = 0.0;
            }
            if (M.data[6][2] != 0.0)
            {
                M.CumulateRowFrom(6, 2, 5, - M.data[6][2] / M.data[2][2]);
                M.data[6][2] = 0.0;
            }
        }

        if (M.data[3][3] == 0.0)
        {
            if (M.data[5][3] != 0.0)
                nb_swaps += M.SwapRowsFrom(3, 5, 3);
            else if (M.data[7][3] != 0.0)
                nb_swaps += M.SwapRowsFrom(3, 7, 3);
        }

        if (M.data[3][3] != 0.0)
        {
            if (M.data[5][3] != 0.0)
                M.CumulateRowFrom(5, 2, 3, - M.data[5][2] / M.data[3][3]);
            if (M.data[7][3] != 0.0)
                M.CumulateRowFrom(7, 2, 3, - M.data[7][2] / M.data[3][3]);
        }

        return Calculus.sgn(nb_swaps) * M.data[0][0] * M.data[2][2] * M.data[3][3] *
                M.MakeSquareBlock(4, 7, 4, 7).Determinant();
    }

    private SquareMatrix CreateMatrixMd()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[0][3] = X_1;
        M.data[1][3] = Y_1;
        M.data[2][3] = X_2;
        M.data[3][3] = Y_2;
        M.data[4][3] = X_3;
        M.data[5][3] = Y_3;
        M.data[6][3] = X_4;
        M.data[7][3] = Y_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private double DeterminantMatrixMd(SquareMatrix M)
    {
        int nb_swaps = 0;

        nb_swaps += M.SwapColumns(0, 2);
        nb_swaps += M.SwapColumns(1, 5);

        M.CumulateRowFrom(2, 0, 2, -1.0);
        M.CumulateRowFrom(4, 0, 2, -1.0);
        M.CumulateRowFrom(6, 0, 2, -1.0);
        M.NullifyColumnFrom(0, 2);

        M.CumulateRowFrom(3, 1, 3, -1.0);
        M.CumulateRowFrom(5, 1, 3, -1.0);
        M.CumulateRowFrom(7, 1, 3, -1.0);
        M.NullifyColumnFrom(1, 3);

        nb_swaps += M.SwapColumnsFrom(3, 5, 2);

        nb_swaps += M.SwapRowsFrom(3, 6, 2);

        if (M.data[2][2] == 0.0)
        {
            if (M.data[3][2] != 0.0)
                nb_swaps += M.SwapRowsFrom(2, 3, 2);
            else if (M.data[4][2] != 0.0)
                nb_swaps += M.SwapRowsFrom(2, 4, 2);
        }

        if (M.data[2][2] != 0.0)
        {
            if (M.data[3][2] != 0.0)
                M.CumulateRow(3, 2, - M.data[3][2] / M.data[2][2]);
            if (M.data[4][2] != 0.0)
                M.CumulateRow(4, 2, - M.data[4][2] / M.data[2][2]);
        }

        if (M.data[3][3] != 0.0)
        {
            if (M.data[4][3] != 0.0)
                M.CumulateRowFrom(4, 3, 3, - M.data[4][3] / M.data[3][3]);
        }
        else if (M.data[4][3] != 0.0)
            nb_swaps += M.SwapRowsFrom(3, 4, 3);

        return Calculus.sgn(nb_swaps) * M.data[2][2] * M.data[3][3] * M.MakeSquareBlock(4, 7, 4, 7).Determinant();
    }

    private SquareMatrix CreateMatrixMe()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[0][4] = X_1;
        M.data[1][4] = Y_1;
        M.data[2][4] = X_2;
        M.data[3][4] = Y_2;
        M.data[4][4] = X_3;
        M.data[5][4] = Y_3;
        M.data[6][4] = X_4;
        M.data[7][4] = Y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private SquareMatrix CreateMatrixMf()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[0][5] = X_1;
        M.data[1][5] = Y_1;
        M.data[2][5] = X_2;
        M.data[3][5] = Y_2;
        M.data[4][5] = X_3;
        M.data[5][5] = Y_3;
        M.data[6][5] = X_4;
        M.data[7][5] = Y_4;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private double DeterminantMatrixMf(SquareMatrix M)
    {
        int nb_swaps = 0;

        nb_swaps += M.SwapColumns(0, 2);

        M.CumulateRow(2, 0, -1.0);
        M.CumulateRow(4, 0, -1.0);
        M.CumulateRow(6, 0, -1.0);

        nb_swaps += M.SwapRows(1, 6);
        nb_swaps += M.SwapRows(3, 4);

        if (M.data[1][1] == 0.0)
        {
            if (M.data[2][1] != 0.0)
                nb_swaps += M.SwapRowsFrom(1, 2, 1);
            else if (M.data[3][1] != 0.0)
                nb_swaps += M.SwapRowsFrom(1, 3, 1);
        }

        if (M.data[1][1] != 0.0)
        {
            if (M.data[2][1] != 0.0)
            {
                M.CumulateRowFrom(2, 1, 2, - M.data[2][1] / M.data[1][1]);
                M.data[2][1] = 0.0;
            }
            if (M.data[3][1] != 0.0)
            {
                M.CumulateRowFrom(3, 1, 2, - M.data[3][1] / M.data[1][1]);
                M.data[3][1] = 0.0;
            }
        }

        if (M.data[2][2] != 0.0)
        {
            if (M.data[3][2] != 0.0)
                M.CumulateRow(3, 2, - M.data[3][2] / M.data[2][2]);
        }
        else if (M.data[3][2] != 0.0)
            nb_swaps += M.SwapRows(2, 3);

        return Calculus.sgn(nb_swaps) * M.data[1][1] * M.data[2][2] * M.MakeSquareBlock(3, 7, 3, 7).Determinant();
    }

    private SquareMatrix CreateMatrixMg()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = X_1;
        M.data[1][6] = Y_1;
        M.data[2][6] = X_2;
        M.data[3][6] = Y_2;
        M.data[4][6] = X_3;
        M.data[5][6] = Y_3;
        M.data[6][6] = X_4;
        M.data[7][6] = Y_4;

        M.data[0][7] = -X_1 * y_1;
        M.data[1][7] = -Y_1 * y_1;
        M.data[2][7] = -X_2 * y_2;
        M.data[3][7] = -Y_2 * y_2;
        M.data[4][7] = -X_3 * y_3;
        M.data[5][7] = -Y_3 * y_3;
        M.data[6][7] = -X_4 * y_4;
        M.data[7][7] = -Y_4 * y_4;

        return  M;
    }

    private SquareMatrix CreateMatrixMh()
    {
        SquareMatrix M = new SquareMatrix(8); // Matrix is filled with zero value

        M.data[0][0] = x_1;
        M.data[2][0] = x_2;
        M.data[4][0] = x_3;
        M.data[6][0] = x_4;

        M.data[0][1] = y_1;
        M.data[2][1] = y_2;
        M.data[4][1] = y_3;
        M.data[6][1] = y_4;

        M.data[0][2] = 1.0;
        M.data[2][2] = 1.0;
        M.data[4][2] = 1.0;
        M.data[6][2] = 1.0;

        M.data[1][3] = x_1;
        M.data[3][3] = x_2;
        M.data[5][3] = x_3;
        M.data[7][3] = x_4;

        M.data[1][4] = y_1;
        M.data[3][4] = y_2;
        M.data[5][4] = y_3;
        M.data[7][4] = y_4;

        M.data[1][5] = 1.0;
        M.data[3][5] = 1.0;
        M.data[5][5] = 1.0;
        M.data[7][5] = 1.0;

        M.data[0][6] = -X_1 * x_1;
        M.data[1][6] = -Y_1 * x_1;
        M.data[2][6] = -X_2 * x_2;
        M.data[3][6] = -Y_2 * x_2;
        M.data[4][6] = -X_3 * x_3;
        M.data[5][6] = -Y_3 * x_3;
        M.data[6][6] = -X_4 * x_4;
        M.data[7][6] = -Y_4 * x_4;

        M.data[0][7] = X_1;
        M.data[1][7] = Y_1;
        M.data[2][7] = X_2;
        M.data[3][7] = Y_2;
        M.data[4][7] = X_3;
        M.data[5][7] = Y_3;
        M.data[6][7] = X_4;
        M.data[7][7] = Y_4;

        return  M;
    }
}
