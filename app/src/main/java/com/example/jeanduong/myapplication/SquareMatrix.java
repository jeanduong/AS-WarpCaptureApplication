package com.example.jeanduong.myapplication;

import java.util.Arrays;

public class SquareMatrix {

    int dimension = 2;
    double[][] data = new double[2][2];

    // Constructors

    SquareMatrix(int dim, double d)
    {
        dimension = dim;
        data = new double[dimension][dimension];
        Arrays.fill(data, d);
    }

    SquareMatrix(int dim)
    {
        dimension = dim;
        data = new double[dimension][dimension];
        Arrays.fill(data, 0.0);
    }

    SquareMatrix(SquareMatrix M)
    {
        dimension = M.dimension;
        data = new double[dimension][dimension];

        for (int r = 0; r < dimension; ++r)
            for (int c = 0; c < dimension; ++c)
                this.data[r][c] = M.data[r][c];
    }

    // Multiply all cells by a constant

    public void ScaleCells(double factor)
    {
        if (factor != 1.0)
        {
            if (factor == 0.0)
                Arrays.fill(data, 0.0);
            else
            {
                for (int r = 0; r < dimension; ++r)
                    for (int c = 0; c < dimension; ++c)
                        this.data[r][c] *= factor;
            }
        }
    }

    public SquareMatrix MakeSquareBlock(int r_begin, int r_end, int c_begin, int c_end)
    {
        if ((r_begin < dimension) && (r_end < dimension) &&
                (c_begin <dimension) && (c_end <dimension) &&
                (r_begin <= r_end) && (c_begin <= c_end) && (r_end - r_begin == c_end - c_begin))
        {
            int dim = r_end - r_begin + 1;
            SquareMatrix Block = new SquareMatrix(dim);

            int i = 0;

            for (int r = r_begin; r <= r_end; ++r)
            {
                int j = 0;

                for (int c = c_begin; c <= c_end; ++c)
                {
                    Block.data[i][j] = this.data[r][c];

                    ++j;
                }

                ++i;
            }
        }

        return new SquareMatrix(0);
    }

    public SquareMatrix MakeMinor(int r, int c)
    {
        SquareMatrix M = new SquareMatrix(dimension - 1);
        int rr = 0;

        for (int i = 0; i < dimension; ++i)
        {
            if (i != r)
            {
                int cc = 0;

                for (int j = 0; j < dimension; ++j)
                {
                    if (j != c)
                    {
                        M.data[rr][cc] = this.data[i][j];
                        ++cc;
                    }
                }

                ++rr;
            }
        }

        return  M;
    }

    ///////////////
    // Swap rows //
    ///////////////

    // Swap contents of rows i and j between tow given positions
    public int SwapRowsWithin(int i, int j, int p_begin, int p_end)
    {
        if ((i < dimension) && (j < dimension)  && (p_begin < dimension) && (p_end < dimension) && (p_begin <= p_end))
        {
            double val;

            for (int k = p_begin; k <= p_end; ++k)
            {
                val = data[i][k];
                data[i][k] = data[j][k];
                data[j][k] = val;
            }

            return 1;
        }

        return 0;
    }

    // Swap contents of rows i and j, from 0 to position p
    public int SwapRowsTo(int i, int j, int p)
    {
        if ((i < dimension) && (j < dimension) && (p < dimension))
            return SwapRowsWithin(i, j, 0, p);

        return 0;
    }

    // Swap contents of rows i and j, from position p to the end
    public int SwapRowsFrom(int i, int j, int p)
    {
        if ((i < dimension) && (j < dimension) && (p < dimension))
            return SwapRowsWithin(i, j, p, dimension - 1);

        return 0;
    }

    // Swap contents of rows i and j
    public int SwapRows(int i, int j)
    {
        if ((i < dimension) && (j < dimension))
            return SwapRowsWithin(i, j, 0, dimension - 1);

        return 0;
    }

    //////////////////
    // Nullify rows //
    //////////////////

    // Replace contents of row i by 0 between tow given positions
    public void NullifyRowWithin(int i, int p_begin, int p_end)
    {
        if ((i < dimension) && (p_begin < dimension) && (p_end < dimension) && (p_begin <= p_end))
            for (int k = p_begin; k <= p_end; ++k) data[i][k] = 0.0;
    }

    // Replace contents of row i by 0 from 0 to a given position
    public void NullifyRowTo(int i, int p)
    {
        NullifyRowWithin(i, 0, p);
    }

    // Replace contents of row i by 0 from 0 to a given position
    public void NullifyRowFrom(int i, int p)
    {
        NullifyRowWithin(i, p, dimension - 1);
    }

    // Replace contents of column i by 0 between tow given positions
    public void NullifyColumnWithin(int i, int p_begin, int p_end)
    {
        if ((i < dimension) && (p_begin < dimension) && (p_end < dimension) && (p_begin <= p_end))
            for (int k = p_begin; k <= p_end; ++k) data[k][i] = 0.0;
    }

    // Replace contents of column i by 0 from 0 to a given position
    public void NullifyColumnFrom(int i, int p)
    {
        NullifyColumnWithin(i, p, dimension - 1);
    }

    //////////////////////////////////////////////////////////
    // Replace content of a row with content of another one //
    //////////////////////////////////////////////////////////

    // Replace content of row i with content of row j between tow given positions
    public void ReplaceRowWithin(int i, int j, int p_begin, int p_end)
    {
        if ((i < dimension) && (j < dimension)  && (p_begin < dimension) && (p_end < dimension) && (p_begin <= p_end))
            for (int k = p_begin; k <= p_end; ++k) data[i][k] = data[j][k];
    }

    // Replace content of row i with content of row j from a given position to the end
    public void ReplaceRowTo(int i, int j, int p)
    {
        if ((i < dimension) && (j < dimension)  && (p < dimension))
            ReplaceRowWithin(i, j, 0, p);
    }

    // Replace content of row i with content of row j from a given position to the end
    public void ReplaceRowFrom(int i, int j, int p)
    {
        if ((i < dimension) && (j < dimension)  && (p < dimension))
            ReplaceRowWithin(i, j, p, dimension - 1);
    }

    ///////////////////////////////////////////////////////////
    // Cumulate content of a row with content of another one //
    ///////////////////////////////////////////////////////////

    // Add weighted row j to row i within a range
    public void CumulateRowWithin(int i, int j, int p_begin, int p_end, double w)
    {
        if ((i < dimension) && (j < dimension) && (p_begin < dimension) && (p_end < dimension) && (p_begin <= p_end))
        {
            if (w == 1.0)
            {
                for (int k = p_begin; k <= p_end; ++k) data[i][k] += data[j][k];
            }
            else if (w == -1.0)
            {
                for (int k = p_begin; k <= p_end; ++k) data[i][k] -= data[j][k];
            }
            else if (w != 0.0)
            {
                for (int k = p_begin; k <= p_end; ++k) data[i][k] += w * data[j][k];
            }
        }
    }

    // Add weighted row j to row i
    public void CumulateRowFrom(int i, int j, int p, double w)
    {
        CumulateRowWithin(i, j, p, dimension - 1, w);
    }

    // Add weighted row j to row i
    public void CumulateRow(int i, int j, double w)
    {
        CumulateRowWithin(i, j, 0, dimension - 1, w);
    }

    ///////////////
    // Swap rows //
    ///////////////

    // Swap contents of columns i and j between tow given positions
    public int SwapColumnsWithin(int i, int j, int p_begin, int p_end)
    {
        if ((i < dimension) && (j < dimension) && (p_begin < dimension) && (p_end < dimension) && (p_begin <= p_end))
        {
            double val;

            for (int k = p_begin; k <= p_end; ++k)
            {
                val = data[k][i];
                data[k][i] = data[k][j];
                data[k][j] = val;
            }

            return 1;
        }

        return 0;
    }

    public int SwapColumnsFrom(int i, int j, int p)
    {
        return SwapColumnsWithin(i, j, p, dimension - 1);
    }

    public int SwapColumns(int i, int j)
    {
        return SwapColumnsWithin(i, j, 0, dimension - 1);
    }

    // Product of diagonal terms

    public double DiagonalProduct()
    {
        double prod = 1.0;

        for (int k = 0; k < dimension; ++k) prod *= data[k][k];

        return prod;
    }

    public double Determinant()
    {
        if (dimension == 2) return data[0][0] * data[1][1] - data[0][1] * data[1][0];

        double d = 0.0;

        if (dimension == 3) // Sarrus formula
        {
            d += data[0][0] * data[1][1] * data[2][2];
            d += data[1][0] * data[2][1] * data[0][2];
            d += data[2][0] * data[0][1] * data[1][2];
            d -= data[0][2] * data[1][1] * data[2][0];
            d -= data[1][2] * data[2][1] * data[0][0];
            d -= data[2][2] * data[0][1] * data[1][0];

            return d;
        }

        // For greater matrices, try to develop considering sparsest column or row

        int min_row_pop = dimension;
        int min_col_pop = dimension;
        int argmin_row = 0;
        int argmin_col = 0;

        for (int i = 0; i < dimension; ++i) // Search sparsest row and sparsest column
        {
            int row_pop = 0;
            int col_pop = 0;

            for (int k = 0; k < dimension; ++k)
            {
                if (data[i][k] != 0.0) ++row_pop;
                if (data[k][i] != 0.0) ++col_pop;
            }

            if (row_pop < min_row_pop)
            {
                min_row_pop = row_pop;
                argmin_row = i;
            }

            if (col_pop < min_col_pop)
            {
                min_col_pop = col_pop;
                argmin_col = i;
            }
        }

        if (min_row_pop < min_col_pop)
        {
            for (int k = 0; k < dimension; ++k)
            {
                if (data[argmin_row][k] != 0.0)
                    d += Calculus.sgn(argmin_row + k) * data[argmin_row][k] * (MakeMinor(argmin_row, k)).Determinant();
            }
        }
        else
        {
            for (int k = 0; k < dimension; ++k)
            {
                if (data[k][argmin_col] != 0.0)
                    d += Calculus.sgn(k + argmin_col) * data[k][argmin_col] * (MakeMinor(k, argmin_col)).Determinant();
            }
        }

        return d;
    }
}
