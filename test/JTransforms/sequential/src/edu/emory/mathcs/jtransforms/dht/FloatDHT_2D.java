/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is JTransforms.
 *
 * The Initial Developer of the Original Code is
 * Piotr Wendykier, Emory University.
 * Portions created by the Initial Developer are Copyright (C) 2007-2009
 * the Initial Developer. All Rights Reserved.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** */

package edu.emory.mathcs.jtransforms.dht;

import edu.emory.mathcs.utils.ConcurrencyUtils;

/**
 * Computes 2D Discrete Hartley Transform (DHT) of real, single precision data.
 * The sizes of both dimensions can be arbitrary numbers. This is a parallel
 * implementation optimized for SMP systems.<br>
 * <br>
 * Part of code is derived from General Purpose FFT Package written by Takuya Ooura
 * (http://www.kurims.kyoto-u.ac.jp/~ooura/fft.html)
 * 
 * @author Piotr Wendykier (piotr.wendykier@gmail.com)
 * 
 */
public class FloatDHT_2D {

    private int rows;

    private int columns;

    private float[] t;

    private FloatDHT_1D dhtColumns, dhtRows;

    private int oldNthreads;

    private int nt;

    private boolean isPowerOfTwo = false;

    private boolean useThreads = false;

    /**
     * Creates new instance of FloatDHT_2D.
     * 
     * @param rows
     *            number of rows
     * @param column
     *            number of columns
     */
    public FloatDHT_2D(int rows, int column) {
        if (rows <= 1 || column <= 1) {
            throw new IllegalArgumentException("rows and columns must be greater than 1");
        }
        this.rows = rows;
        this.columns = column;
        if (rows * column >= ConcurrencyUtils.getThreadsBeginN_2D()) {
            this.useThreads = true;
        }
        if (ConcurrencyUtils.isPowerOf2(rows) && ConcurrencyUtils.isPowerOf2(column)) {
            isPowerOfTwo = true;
            oldNthreads = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;
            nt = 4 * oldNthreads * rows;
            if (column == 2 * oldNthreads) {
                nt >>= 1;
            } else if (column < 2 * oldNthreads) {
                nt >>= 2;
            }
            t = new float[nt];
        }
        dhtColumns = new FloatDHT_1D(column);
        if (column == rows) {
            dhtRows = dhtColumns;
        } else {
            dhtRows = new FloatDHT_1D(rows);
        }
    }

    /**
     * Computes 2D real, forward DHT leaving the result in <code>a</code>. The
     * data is stored in 1D array in row-major order.
     * 
     * @param a
     *            data to transform
     */
    public void forward(final float[] a) {
        int nthreads = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;
        if (isPowerOfTwo) {
            if (nthreads != oldNthreads) {
                nt = 4 * nthreads * rows;
                if (columns == 2 * nthreads) {
                    nt >>= 1;
                } else if (columns < 2 * nthreads) {
                    nt >>= 2;
                }
                t = new float[nt];
                oldNthreads = nthreads;
            }
            if ((nthreads > 1) && useThreads) {
                ddxt2d_subth(-1, a, true);
                ddxt2d0_subth(-1, a, true);
            } else {
                ddxt2d_sub(-1, a, true);
                for (int i = 0; i < rows; i++) {
                    dhtColumns.forward(a, i * columns);
                }
            }
            yTransform(a);
        } else {
        	 {
        		 {    
        			for (int i = 0; i < rows; i++) {
        				dhtColumns.forward(a, i * columns);
        			}
        		}
        	}
        	float[] temp = new float[rows];
        	 {
        		 {    
        			for (int c = 0; c < columns; c++) {
        				for (int r = 0; r < rows; r++) {
        					temp[r] = a[r * columns + c];
        				}
        				dhtRows.forward(temp);
        				for (int r = 0; r < rows; r++) {
        					a[r * columns + c] = temp[r];
        				}
        			}
        		}
        	}
        	yTransform(a);
        }
    }

    /**
     * Computes 2D real, forward DHT leaving the result in <code>a</code>. The
     * data is stored in 2D array.
     * 
     * @param a
     *            data to transform
     */
    public void forward(final float[][] a) {
        int nthreads = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;
        if (isPowerOfTwo) {
            if (nthreads != oldNthreads) {
                nt = 4 * nthreads * rows;
                if (columns == 2 * nthreads) {
                    nt >>= 1;
                } else if (columns < 2 * nthreads) {
                    nt >>= 2;
                }
                t = new float[nt];
                oldNthreads = nthreads;
            }
            if ((nthreads > 1) && useThreads) {
                ddxt2d_subth(-1, a, true);
                ddxt2d0_subth(-1, a, true);
            } else {
                ddxt2d_sub(-1, a, true);
                for (int i = 0; i < rows; i++) {
                    dhtColumns.forward(a[i]);
                }
            }
            y_transform(a);
        } else {
        	 {
        		 {    
        			for (int i = 0; i < rows; i++) {
        				dhtColumns.forward(a[i]);
        			}
        		}
        	}
        	float[] temp = new float[rows];
        	 {
        		 {    
        			for (int c = 0; c < columns; c++) {
        				for (int r = 0; r < rows; r++) {
        					temp[r] = a[r][c];
        				}
        				dhtRows.forward(temp);
        				for (int r = 0; r < rows; r++) {
        					a[r][c] = temp[r];
        				}
        			}
        		}
        	}
        	y_transform(a);
        }
    }

    /**
     * Computes 2D real, inverse DHT leaving the result in <code>a</code>. The
     * data is stored in 1D array in row-major order.
     * 
     * @param a
     *            data to transform
     * @param scale
     *            if true then scaling is performed
     */
    public void inverse(final float[] a, final boolean scale) {
        int nthreads = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;
        if (isPowerOfTwo) {
            if (nthreads != oldNthreads) {
                nt = 4 * nthreads * rows;
                if (columns == 2 * nthreads) {
                    nt >>= 1;
                } else if (columns < 2 * nthreads) {
                    nt >>= 2;
                }
                t = new float[nt];
                oldNthreads = nthreads;
            }
            if ((nthreads > 1) && useThreads) {
                ddxt2d_subth(1, a, scale);
                ddxt2d0_subth(1, a, scale);
            } else {
                ddxt2d_sub(1, a, scale);
                for (int i = 0; i < rows; i++) {
                    dhtColumns.inverse(a, i * columns, scale);
                }
            }
            yTransform(a);
        } else {
        	 {
        		 {    
        			for (int i = 0; i < rows; i++) {
        				dhtColumns.inverse(a, i * columns, scale);
        			}
        		}
        	}
        	float[] temp = new float[rows];
        	 {
        		 {    
        			for (int c = 0; c < columns; c++) {
        				for (int r = 0; r < rows; r++) {
        					temp[r] = a[r * columns + c];
        				}
        				dhtRows.inverse(temp, scale);
        				for (int r = 0; r < rows; r++) {
        					a[r * columns + c] = temp[r];
        				}
        			}
        		}
        	}
        	yTransform(a);
        }
    }

    /**
     * Computes 2D real, inverse DHT leaving the result in <code>a</code>. The
     * data is stored in 2D array.
     * 
     * @param a
     *            data to transform
     * @param scale
     *            if true then scaling is performed
     */
    public void inverse(final float[][] a, final boolean scale) {
        int nthreads = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;
        if (isPowerOfTwo) {
            if (nthreads != oldNthreads) {
                nt = 4 * nthreads * rows;
                if (columns == 2 * nthreads) {
                    nt >>= 1;
                } else if (columns < 2 * nthreads) {
                    nt >>= 2;
                }
                t = new float[nt];
                oldNthreads = nthreads;
            }
            if ((nthreads > 1) && useThreads) {
                ddxt2d_subth(1, a, scale);
                ddxt2d0_subth(1, a, scale);
            } else {
                ddxt2d_sub(1, a, scale);
                for (int i = 0; i < rows; i++) {
                    dhtColumns.inverse(a[i], scale);
                }
            }
            y_transform(a);
        } else {
        	 {
        		 {    
        			for (int i = 0; i < rows; i++) {
        				dhtColumns.inverse(a[i], scale);
        			}
        		}
        	}
        	float[] temp = new float[rows];
        	 {
        		 {    
        			for (int c = 0; c < columns; c++) {
        				for (int r = 0; r < rows; r++) {
        					temp[r] = a[r][c];
        				}
        				dhtRows.inverse(temp, scale);
        				for (int r = 0; r < rows; r++) {
        					a[r][c] = temp[r];
        				}
        			}
        		}
        	}
        	y_transform(a);
        }
    }

    private void ddxt2d_subth(final int isgn, final float[] a, final boolean scale) {
        int nthread = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;
        int nt = 4 * rows;
        if (columns == 2 * nthread) {
            nt >>= 1;
        } else if (columns < 2 * nthread) {
            nthread = columns;
            nt >>= 2;
        }
        final int nthreads = nthread;
         {
        	 {    
        		for (int i = 0; i < nthreads; i++) {
        			final int n0 = i;
        			final int startt = nt * i;
        			int idx1, idx2;
        			if (columns > 2 * nthreads) {
        				if (isgn == -1) {
        					for (int c = 4 * n0; c < columns; c += 4 * nthreads) {
        						for (int r = 0; r < rows; r++) {
        							idx1 = r * columns + c;
        							idx2 = startt + rows + r;
        							t[startt + r] = a[idx1];
        							t[idx2] = a[idx1 + 1];
        							t[idx2 + rows] = a[idx1 + 2];
        							t[idx2 + 2 * rows] = a[idx1 + 3];
                                }
                                dhtRows.forward(t, startt);
                                dhtRows.forward(t, startt + rows);
                                dhtRows.forward(t, startt + 2 * rows);
                                dhtRows.forward(t, startt + 3 * rows);
                                for (int r = 0; r < rows; r++) {
                                    idx1 = r * columns + c;
                                    idx2 = startt + rows + r;
                                    a[idx1] = t[startt + r];
                                    a[idx1 + 1] = t[idx2];
                                    a[idx1 + 2] = t[idx2 + rows];
                                    a[idx1 + 3] = t[idx2 + 2 * rows];
                                }
                            }
                        } else {
                            for (int c = 4 * n0; c < columns; c += 4 * nthreads) {
                                for (int r = 0; r < rows; r++) {
                                    idx1 = r * columns + c;
                                    idx2 = startt + rows + r;
                                    t[startt + r] = a[idx1];
                                    t[idx2] = a[idx1 + 1];
                                    t[idx2 + rows] = a[idx1 + 2];
                                    t[idx2 + 2 * rows] = a[idx1 + 3];
                                }
                                dhtRows.inverse(t, startt, scale);
                                dhtRows.inverse(t, startt + rows, scale);
                                dhtRows.inverse(t, startt + 2 * rows, scale);
                                dhtRows.inverse(t, startt + 3 * rows, scale);
                                for (int r = 0; r < rows; r++) {
                                    idx1 = r * columns + c;
                                    idx2 = startt + rows + r;
                                    a[idx1] = t[startt + r];
                                    a[idx1 + 1] = t[idx2];
                                    a[idx1 + 2] = t[idx2 + rows];
                                    a[idx1 + 3] = t[idx2 + 2 * rows];
                                }
                            }
                        }
                    } else if (columns == 2 * nthreads) {
                        for (int r = 0; r < rows; r++) {
                            idx1 = r * columns + 2 * n0;
                            idx2 = startt + r;
                            t[idx2] = a[idx1];
                            t[idx2 + rows] = a[idx1 + 1];
                        }
                        if (isgn == -1) {
                            dhtRows.forward(t, startt);
                            dhtRows.forward(t, startt + rows);
                        } else {
                            dhtRows.inverse(t, startt, scale);
                            dhtRows.inverse(t, startt + rows, scale);
                        }
                        for (int r = 0; r < rows; r++) {
                            idx1 = r * columns + 2 * n0;
                            idx2 = startt + r;
                            a[idx1] = t[idx2];
                            a[idx1 + 1] = t[idx2 + rows];
                        }
                    } else if (columns == nthreads) {
                        for (int r = 0; r < rows; r++) {
                            t[startt + r] = a[r * columns + n0];
                        }
                        if (isgn == -1) {
                            dhtRows.forward(t, startt);
                        } else {
                            dhtRows.inverse(t, startt, scale);
                        }
                        for (int r = 0; r < rows; r++) {
                            a[r * columns + n0] = t[startt + r];
                        }
                    }
                }
            }
        }
    }

    private void ddxt2d_subth(final int isgn, final float[][] a, final boolean scale) {
        int nthread = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;
        int nt = 4 * rows;
        if (columns == 2 * nthread) {
            nt >>= 1;
        } else if (columns < 2 * nthread) {
            nthread = columns;
            nt >>= 2;
        }
        final int nthreads = nthread;
         {
        	 {    
        		for (int i = 0; i < nthreads; i++) {
        			final int n0 = i;
        			final int startt = nt * i;
        			int idx2;
        			if (columns > 2 * nthreads) {
        				if (isgn == -1) {
        					for (int c = 4 * n0; c < columns; c += 4 * nthreads) {
        						for (int r = 0; r < rows; r++) {
        							idx2 = startt + rows + r;
        							t[startt + r] = a[r][c];
        							t[idx2] = a[r][c + 1];
        							t[idx2 + rows] = a[r][c + 2];
        							t[idx2 + 2 * rows] = a[r][c + 3];
        						}
        						dhtRows.forward(t, startt);
                                dhtRows.forward(t, startt + rows);
                                dhtRows.forward(t, startt + 2 * rows);
                                dhtRows.forward(t, startt + 3 * rows);
                                for (int r = 0; r < rows; r++) {
                                    idx2 = startt + rows + r;
                                    a[r][c] = t[startt + r];
                                    a[r][c + 1] = t[idx2];
                                    a[r][c + 2] = t[idx2 + rows];
                                    a[r][c + 3] = t[idx2 + 2 * rows];
                                }
                            }
                        } else {
                            for (int c = 4 * n0; c < columns; c += 4 * nthreads) {
                                for (int r = 0; r < rows; r++) {
                                    idx2 = startt + rows + r;
                                    t[startt + r] = a[r][c];
                                    t[idx2] = a[r][c + 1];
                                    t[idx2 + rows] = a[r][c + 2];
                                    t[idx2 + 2 * rows] = a[r][c + 3];
                                }
                                dhtRows.inverse(t, startt, scale);
                                dhtRows.inverse(t, startt + rows, scale);
                                dhtRows.inverse(t, startt + 2 * rows, scale);
                                dhtRows.inverse(t, startt + 3 * rows, scale);
                                for (int r = 0; r < rows; r++) {
                                    idx2 = startt + rows + r;
                                    a[r][c] = t[startt + r];
                                    a[r][c + 1] = t[idx2];
                                    a[r][c + 2] = t[idx2 + rows];
                                    a[r][c + 3] = t[idx2 + 2 * rows];
                                }
                            }
                        }
                    } else if (columns == 2 * nthreads) {
                        for (int r = 0; r < rows; r++) {
                            idx2 = startt + r;
                            t[idx2] = a[r][2 * n0];
                            t[idx2 + rows] = a[r][2 * n0 + 1];
                        }
                        if (isgn == -1) {
                            dhtRows.forward(t, startt);
                            dhtRows.forward(t, startt + rows);
                        } else {
                            dhtRows.inverse(t, startt, scale);
                            dhtRows.inverse(t, startt + rows, scale);
                        }
                        for (int r = 0; r < rows; r++) {
                            idx2 = startt + r;
                            a[r][2 * n0] = t[idx2];
                            a[r][2 * n0 + 1] = t[idx2 + rows];
                        }
                    } else if (columns == nthreads) {
                        for (int r = 0; r < rows; r++) {
                            t[startt + r] = a[r][n0];
                        }
                        if (isgn == -1) {
                            dhtRows.forward(t, startt);
                        } else {
                            dhtRows.inverse(t, startt, scale);
                        }
                        for (int r = 0; r < rows; r++) {
                            a[r][n0] = t[startt + r];
                        }
                    }
                }
            }
        }
    }

    private void ddxt2d0_subth(final int isgn, final float[] a, final boolean scale) {
        final int nthreads = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE > rows ? rows : ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;

         {
        	 {    
        		for (int i = 0; i < nthreads; i++) {
        			final int n0 = i;
        			if (isgn == -1) {
        				for (int r = n0; r < rows; r += nthreads) {
        					dhtColumns.forward(a, r * columns);
        				}
                    } else {
                        for (int r = n0; r < rows; r += nthreads) {
                            dhtColumns.inverse(a, r * columns, scale);
                        }
                    }
                }
            }
        }
    }

    private void ddxt2d0_subth(final int isgn, final float[][] a, final boolean scale) {
    	final int nthreads = ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE > rows ? rows : ConcurrencyUtils.WORKSTEALING_TASKS_PARTITION_SIZE;

    	 {
    		 {    
    			for (int i = 0; i < nthreads; i++) {
    				final int n0 = i;
    				if (isgn == -1) {
    					for (int r = n0; r < rows; r += nthreads) {
                            dhtColumns.forward(a[r]);
                        }
                    } else {
                        for (int r = n0; r < rows; r += nthreads) {
                            dhtColumns.inverse(a[r], scale);
                        }
                    }
                }
            }
        }
    }

    private void ddxt2d_sub(int isgn, float[] a, boolean scale) {
        int idx1, idx2;

        if (columns > 2) {
            if (isgn == -1) {
                for (int c = 0; c < columns; c += 4) {
                    for (int r = 0; r < rows; r++) {
                        idx1 = r * columns + c;
                        idx2 = rows + r;
                        t[r] = a[idx1];
                        t[idx2] = a[idx1 + 1];
                        t[idx2 + rows] = a[idx1 + 2];
                        t[idx2 + 2 * rows] = a[idx1 + 3];
                    }
                    dhtRows.forward(t, 0);
                    dhtRows.forward(t, rows);
                    dhtRows.forward(t, 2 * rows);
                    dhtRows.forward(t, 3 * rows);
                    for (int r = 0; r < rows; r++) {
                        idx1 = r * columns + c;
                        idx2 = rows + r;
                        a[idx1] = t[r];
                        a[idx1 + 1] = t[idx2];
                        a[idx1 + 2] = t[idx2 + rows];
                        a[idx1 + 3] = t[idx2 + 2 * rows];
                    }
                }
            } else {
                for (int c = 0; c < columns; c += 4) {
                    for (int r = 0; r < rows; r++) {
                        idx1 = r * columns + c;
                        idx2 = rows + r;
                        t[r] = a[idx1];
                        t[idx2] = a[idx1 + 1];
                        t[idx2 + rows] = a[idx1 + 2];
                        t[idx2 + 2 * rows] = a[idx1 + 3];
                    }
                    dhtRows.inverse(t, 0, scale);
                    dhtRows.inverse(t, rows, scale);
                    dhtRows.inverse(t, 2 * rows, scale);
                    dhtRows.inverse(t, 3 * rows, scale);
                    for (int r = 0; r < rows; r++) {
                        idx1 = r * columns + c;
                        idx2 = rows + r;
                        a[idx1] = t[r];
                        a[idx1 + 1] = t[idx2];
                        a[idx1 + 2] = t[idx2 + rows];
                        a[idx1 + 3] = t[idx2 + 2 * rows];
                    }
                }
            }
        } else if (columns == 2) {
            for (int r = 0; r < rows; r++) {
                idx1 = r * columns;
                t[r] = a[idx1];
                t[rows + r] = a[idx1 + 1];
            }
            if (isgn == -1) {
                dhtRows.forward(t, 0);
                dhtRows.forward(t, rows);
            } else {
                dhtRows.inverse(t, 0, scale);
                dhtRows.inverse(t, rows, scale);
            }
            for (int r = 0; r < rows; r++) {
                idx1 = r * columns;
                a[idx1] = t[r];
                a[idx1 + 1] = t[rows + r];
            }
        }
    }

    private void ddxt2d_sub(int isgn, float[][] a, boolean scale) {
        int idx2;

        if (columns > 2) {
            if (isgn == -1) {
                for (int c = 0; c < columns; c += 4) {
                    for (int r = 0; r < rows; r++) {
                        idx2 = rows + r;
                        t[r] = a[r][c];
                        t[idx2] = a[r][c + 1];
                        t[idx2 + rows] = a[r][c + 2];
                        t[idx2 + 2 * rows] = a[r][c + 3];
                    }
                    dhtRows.forward(t, 0);
                    dhtRows.forward(t, rows);
                    dhtRows.forward(t, 2 * rows);
                    dhtRows.forward(t, 3 * rows);
                    for (int r = 0; r < rows; r++) {
                        idx2 = rows + r;
                        a[r][c] = t[r];
                        a[r][c + 1] = t[idx2];
                        a[r][c + 2] = t[idx2 + rows];
                        a[r][c + 3] = t[idx2 + 2 * rows];
                    }
                }
            } else {
                for (int c = 0; c < columns; c += 4) {
                    for (int r = 0; r < rows; r++) {
                        idx2 = rows + r;
                        t[r] = a[r][c];
                        t[idx2] = a[r][c + 1];
                        t[idx2 + rows] = a[r][c + 2];
                        t[idx2 + 2 * rows] = a[r][c + 3];
                    }
                    dhtRows.inverse(t, 0, scale);
                    dhtRows.inverse(t, rows, scale);
                    dhtRows.inverse(t, 2 * rows, scale);
                    dhtRows.inverse(t, 3 * rows, scale);
                    for (int r = 0; r < rows; r++) {
                        idx2 = rows + r;
                        a[r][c] = t[r];
                        a[r][c + 1] = t[idx2];
                        a[r][c + 2] = t[idx2 + rows];
                        a[r][c + 3] = t[idx2 + 2 * rows];
                    }
                }
            }
        } else if (columns == 2) {
            for (int r = 0; r < rows; r++) {
                t[r] = a[r][0];
                t[rows + r] = a[r][1];
            }
            if (isgn == -1) {
                dhtRows.forward(t, 0);
                dhtRows.forward(t, rows);
            } else {
                dhtRows.inverse(t, 0, scale);
                dhtRows.inverse(t, rows, scale);
            }
            for (int r = 0; r < rows; r++) {
                a[r][0] = t[r];
                a[r][1] = t[rows + r];
            }
        }
    }

    private void yTransform(float[] a) {
        int mRow, mCol, idx1, idx2;
        float A, B, C, D, E;
        for (int r = 0; r <= rows / 2; r++) {
            mRow = (rows - r) % rows;
            idx1 = r * columns;
            idx2 = mRow * columns;
            for (int c = 0; c <= columns / 2; c++) {
                mCol = (columns - c) % columns;
                A = a[idx1 + c];
                B = a[idx2 + c];
                C = a[idx1 + mCol];
                D = a[idx2 + mCol];
                E = ((A + D) - (B + C)) / 2;
                a[idx1 + c] = A - E;
                a[idx2 + c] = B + E;
                a[idx1 + mCol] = C + E;
                a[idx2 + mCol] = D - E;
            }
        }
    }

    private void y_transform(float[][] a) {
        int mRow, mCol;
        float A, B, C, D, E;
        for (int r = 0; r <= rows / 2; r++) {
            mRow = (rows - r) % rows;
            for (int c = 0; c <= columns / 2; c++) {
                mCol = (columns - c) % columns;
                A = a[r][c];
                B = a[mRow][c];
                C = a[r][mCol];
                D = a[mRow][mCol];
                E = ((A + D) - (B + C)) / 2;
                a[r][c] = A - E;
                a[mRow][c] = B + E;
                a[r][mCol] = C + E;
                a[mRow][mCol] = D - E;
            }
        }
    }

}
