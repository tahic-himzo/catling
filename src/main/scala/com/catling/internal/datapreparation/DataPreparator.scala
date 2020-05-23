package com.catling.internal.datapreparation

import com.catling.loadtest.DataPreparator

object DataPreparator {
  def passThrough[A]: DataPreparator[A, A] = in => in
}
