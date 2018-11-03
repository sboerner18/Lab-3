/**
  * Illustrates flatMap + countByValue for wordcount.
  */


import org.apache.spark._

object WordCount {



  def main(args: Array[String]) {

    System.setProperty("hadoop.home.dir","C:\\winutils" )
    //val inputFile = args(0)
    //val outputFile = args(1)
    val conf = new SparkConf().setAppName("wordCount").setMaster("local[*]")
    // Create a Scala Spark Context.
    val sc = new SparkContext(conf)
    // Load our input data.
    //val input =  sc.textFile(inputFile)
    val input = sc.textFile("facebook_small")

    val friendslist = input.map(x => (x.split(" ")(0), x.split(" ")(1))).groupByKey()

    val user = input.flatMap(x => x.split(" ")).distinct()

    val combinations = user.cartesian(user).filter{ case (a,b)  => a < b}
    val combosswapped =  combinations.map( pair => pair.swap)

    val friendsjoined = combinations.join(friendslist).map{case (a,b) =>
      ((a, b._1), b._2)}

    val friendsjoined2 = combosswapped.join(friendslist).map{case (a,b) =>
      ((b._1, a), b._2)}

    val friendsfinal = friendsjoined.fullOuterJoin(friendsjoined2).map{ case ((a,b),(c,d)) =>
      ((a,b), c.toList, d.toList)}.map{ case ((a,b), c, d) => ((a,b), c.intersect(d))}


    for (line <- friendsfinal.collect()) {
      System.out.println("* " + line)
    }


    //words.saveAsTextFile("output")
  }

}
