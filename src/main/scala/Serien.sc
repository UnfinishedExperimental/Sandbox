
import java.nio.file._
import scala.collection.JavaConverters._
import scala.annotation.tailrec

object Serien {
  val root = Paths.get("/home/daniel/Videos/Serien")
                                                  //> root  : java.nio.file.Path = /home/daniel/Videos/Serien
  val toEscape = "[]{}().s"                       //> toEscape  : String = []{}().s
  val other = "-_,"                               //> other  : String = -_,
  val useless = Seq("xvid", "divx", "hdtv", "x264", "aac", "360p", "720p", "1080p", "264")
                                                  //> useless  : Seq[String] = List(xvid, divx, hdtv, x264, aac, 360p, 720p, 1080p
                                                  //| , 264)
  val replace = Seq("ö" -> "oe", "ü" -> "ue", "ä" -> "ae")
                                                  //> replace  : Seq[(String, String)] = List((ö,oe), (ü,ue), (ä,ae))

  def escaped = toEscape flatMap ("\\" + _)       //> escaped: => String

  def spaces = '[' + other + escaped + ']'        //> spaces: => String

  def childrenOf(p: Path) = Files.newDirectoryStream(p).iterator().asScala.toArray
                                                  //> childrenOf: (p: java.nio.file.Path)Array[java.nio.file.Path]

  def isVideo(p: Path) = Files.isRegularFile(p) && Files.probeContentType(p).startsWith("video")
                                                  //> isVideo: (p: java.nio.file.Path)Boolean
  def isDir(p: Path) = Files.isDirectory(p)       //> isDir: (p: java.nio.file.Path)Boolean

  def recursivVideoSearch(p: Seq[Path], found: Seq[Path] = Nil): Seq[Path] = {
    val childs = p flatMap childrenOf
    val vids = childs.filter(isVideo) ++: found
    val dirs = childs filter isDir

    if (dirs.isEmpty)
      vids
    else
      recursivVideoSearch(dirs, vids)
  }                                               //> recursivVideoSearch: (p: Seq[java.nio.file.Path], found: Seq[java.nio.file.P
                                                  //| ath])Seq[java.nio.file.Path]

  val vids = recursivVideoSearch(Seq(root))       //> vids  : Seq[java.nio.file.Path] = List(/home/daniel/Videos/Serien/The.Big.B
                                                  //| ang.Theory.S06E21.HDTV.x264-LOL[rarbg]/Sample/the.big.bang.theory.621.hdtv-
                                                  //| lol.sample.mp4, /home/daniel/Videos/Serien/[ www.Torrenting.com ] - Skins.7
                                                  //| x03.Pure_Part_One.720p_HDTV_x264-FoV/Sample/skins.7x03.pure_part_one.720p_h
                                                  //| dtv_x264-sample-fov.mkv, /home/daniel/Videos/Serien/[ www.Torrenting.com ] 
                                                  //| - The.Walking.Dead.S04E02.720p.HDTV.x264-2HD/Sample/sample-the.walking.dead
                                                  //| .s04e02.720p.hdtv.x264-2hd.mkv, /home/daniel/Videos/Serien/[ www.TorrentDay
                                                  //| .com ] - Game.of.Thrones.S02E04.Garden.of.Bones.HDTV.XviD-FQM/Sample/sample
                                                  //| -game.of.thrones.s02e04.hdtv.xvid-fqm.avi, /home/daniel/Videos/Serien/Game.
                                                  //| of.Thrones.S03E08.SWESUB.720p.HDTV.XviD.AC3-Olizzz/Sample/Sample.avi, /home
                                                  //| /daniel/Videos/Serien/Borgen/Borgen.BBC.S01E01.mp4, /home/daniel/Videos/Ser
                                                  //| ien/The.Big.Bang.Theory.S06E21.HDTV.x264-LOL[rarbg]/the.big.bang.theory.621
                                                  //| .hdtv-lol.mp4, /home/da
                                                  //| Output exceeds cutoff limit.

  val name = (_: Path).getFileName().toString()   //> name  : java.nio.file.Path => String = <function1>

  def stripExtension(s: String) = {
    val p = s.lastIndexOf(".")
    s.substring(0, p)
  }                                               //> stripExtension: (s: String)String

  def tokenize(s: String) = s split spaces filter (_.nonEmpty)
                                                  //> tokenize: (s: String)Array[String]

  val stripUseless = (_: Array[String]).filterNot(t => useless.exists(_.compareToIgnoreCase(t) == 0))
                                                  //> stripUseless  : Array[String] => Array[String] = <function1>

  val replaceStuff = (_: Array[String]).map(token => replace.foldLeft(token)((t, r) => t.replace(r._1, r._2)))
                                                  //> replaceStuff  : Array[String] => Array[String] = <function1>

  val vidTokens = name andThen
    stripExtension andThen
    tokenize andThen
    stripUseless andThen
    replaceStuff andThen
    //(_.filter(_.length() > 3)) andThen
    (_.map(_.toLowerCase).toSet)                  //> vidTokens  : java.nio.file.Path => scala.collection.immutable.Set[String] =
                                                  //|  <function1>

  val tokens = vids.map(v => (v, vidTokens(v)))   //> tokens  : Seq[(java.nio.file.Path, scala.collection.immutable.Set[String])]
                                                  //|  = List((/home/daniel/Videos/Serien/The.Big.Bang.Theory.S06E21.HDTV.x264-LO
                                                  //| L[rarbg]/Sample/the.big.bang.theory.621.hdtv-lol.sample.mp4,Set(lol, big, t
                                                  //| heory, sample, 621, bang, the)), (/home/daniel/Videos/Serien/[ www.Torrenti
                                                  //| ng.com ] - Skins.7x03.Pure_Part_One.720p_HDTV_x264-FoV/Sample/skins.7x03.pu
                                                  //| re_part_one.720p_hdtv_x264-sample-fov.mkv,Set(7x03, fov, pure, skins, sampl
                                                  //| e, part, one)), (/home/daniel/Videos/Serien/[ www.Torrenting.com ] - The.Wa
                                                  //| lking.Dead.S04E02.720p.HDTV.x264-2HD/Sample/sample-the.walking.dead.s04e02.
                                                  //| 720p.hdtv.x264-2hd.mkv,Set(dead, 2hd, walking, s04e02, sample, the)), (/hom
                                                  //| e/daniel/Videos/Serien/[ www.TorrentDay.com ] - Game.of.Thrones.S02E04.Gard
                                                  //| en.of.Bones.HDTV.XviD-FQM/Sample/sample-game.of.thrones.s02e04.hdtv.xvid-fq
                                                  //| m.avi,Set(s02e04, fqm, sample, thrones, game, of)), (/home/daniel/Videos/Se
                                                  //| rien/Game.of.Thrones.S0
                                                  //| Output exceeds cutoff limit.
  def distance(a: Set[String], b: Set[String]) = a & b size
                                                  //> distance: (a: Set[String], b: Set[String])Int

  def groupByOverlapp(t: Seq[(Set[String], Set[Path])]) = {
    t.combinations(2).toSeq.
      groupBy(s => s(0)._1 & s(1)._1).
      mapValues(_.flatten.flatMap(_._2).toSet).toSeq
  }                                               //> groupByOverlapp: (t: Seq[(Set[String], Set[java.nio.file.Path])])Seq[(scala
                                                  //| .collection.immutable.Set[String], scala.collection.immutable.Set[java.nio.
                                                  //| file.Path])]

  def removeLower(sets: Seq[(Set[String], Set[Path])]) = {
    val sorted = sets.sortBy(_._1.size).reverse

    var already = Set[Path]()
    (for ((cross, paths) <- sorted) yield {
      val still = paths &~ already
      already = already | still
      (cross, still)
    }) filter (!_._2.isEmpty)
  }                                               //> removeLower: (sets: Seq[(Set[String], Set[java.nio.file.Path])])Seq[(Set[St
                                                  //| ring], scala.collection.immutable.Set[java.nio.file.Path])]

  val round = groupByOverlapp _ andThen removeLower
                                                  //> round  : Seq[(Set[String], Set[java.nio.file.Path])] => Seq[(Set[String], s
                                                  //| cala.collection.immutable.Set[java.nio.file.Path])] = <function1>

  def process(tokens: Seq[(Path, Set[String])]) = {
    val t = tokens.map(tu => (tu._2, Set(tu._1)))
    round(t)
  }                                               //> process: (tokens: Seq[(java.nio.file.Path, Set[String])])Seq[(Set[String], 
                                                  //| scala.collection.immutable.Set[java.nio.file.Path])]

  def process2(tokens: Seq[(Path, Set[String])]) = {
    val dist = tokens.combinations(2).map(se => (se.map(_._1), distance(se(0)._2, se(1)._2))).toSeq

    val distMap = for ((p, _) <- tokens) yield {
      val o = dist.filter(_._1.contains(p)).map(tu => (tu._1.find(_ != p).get, tu._2)).sortBy(_._2).reverse
      (p, o)
    }

    val best = trans(distMap.toMap mapValues (_.head._1))
    best
  }                                               //> process2: (tokens: Seq[(java.nio.file.Path, Set[String])])Map[java.nio.file
                                                  //| .Path,Iterable[java.nio.file.Path]]

  def trans[K, V](m: Map[K, V]): Map[V, Iterable[K]] = {
    m.groupBy(_._2).mapValues(_.keys)
  }                                               //> trans: [K, V](m: Map[K,V])Map[V,Iterable[K]]

val lines2 = (for ((set, paths) <- process2(tokens)) yield {
    ("----" + name(set)) +: paths.toSeq.map("\t" + name(_))

  }).flatten                                      //> lines2  : scala.collection.immutable.Iterable[String] = List(----redebeta.c
                                                  //| om.the.big.bang.theory.624.hdtv-lol.mp4, "	gmt.307.REDEBETA.COM.mp4", ----h
                                                  //| eute-show vom 17.05.2013 - komplette Folge vom 17.05. _ 17.5. _ 17.5.2013(3
                                                  //| 60p_H.264-AAC).mp4, "	heute-show vom 7.6.2013 - HEUTE-SHOW, 07.06.2013
                                                  //|  22_30(360p_H.264-AAC).mp4", "	ZDF Heute Show 2013 Folge 116 vom 05.04.
                                                  //| 13 in HD(360p_H.264-AAC).mp4", ----Burn.Notice.S06E09.HDTV.x264-ASAP.[VTV].
                                                  //| mp4, "	Burn.Notice.S06E08.HDTV.x264-ASAP.mp4", ----how.i.met.your.mothe
                                                  //| r.821.hdtv-lol.mp4, "	How.I.Met.Your.Mother.S06E24.HDTV.XviD-LOL.[VTV]
                                                  //| .avi", ----Unsere Mütter, Unsere Väter_ Ein anderer Krieg [Teil 2 von 3](
                                                  //| 360p_H.264-AAC).mp4, "	Unsere Mütter, Unsere Väter_ Ein anderes Land 
                                                  //| [Teil 3 von 3](360p_H.264-AAC).mp4", ----Community.S04E11.HDTV.x264-LOL.mp4
                                                  //| , "	Community.S04E12.HDTV.x264-LOL.mp4", ----Unsere Mütter, Unsere 
                                                  //| Väter_ Ein anderes Land [Teil 3 von 3](360p_H.264-AAC).mp
                                                  //| Output exceeds cutoff limit.

  /*val lines = (for ((set, paths) <- process(tokens)) yield {
    set.mkString("-") +: paths.toSeq.map("\t" + name(_))

  }).flatten

  println(lines.take(10).mkString("\n"))
  println(lines.drop(10).take(10).mkString("\n"))
  println(lines.drop(10).drop(10).take(10).mkString("\n"))
  println(lines.drop(10).drop(10).drop(10).take(10).mkString("\n"))
  println(lines.drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
  println(lines.drop(10).drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
  println(lines.drop(10).drop(10).drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
  println(lines.drop(10).drop(10).drop(10).drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
*/

  println(lines2.take(10).mkString("\n"))         //> ----redebeta.com.the.big.bang.theory.624.hdtv-lol.mp4
                                                  //| 	gmt.307.REDEBETA.COM.mp4
                                                  //| ----heute-show vom 17.05.2013 - komplette Folge vom 17.05. _ 17.5. _ 17.5.2
                                                  //| 013(360p_H.264-AAC).mp4
                                                  //| 	heute-show vom 7.6.2013 - HEUTE-SHOW, 07.06.2013 22_30(360p_H.264-AAC).m
                                                  //| p4
                                                  //| 	ZDF Heute Show 2013 Folge 116 vom 05.04.13 in HD(360p_H.264-AAC).mp4
                                                  //| ----Burn.Notice.S06E09.HDTV.x264-ASAP.[VTV].mp4
                                                  //| 	Burn.Notice.S06E08.HDTV.x264-ASAP.mp4
                                                  //| ----how.i.met.your.mother.821.hdtv-lol.mp4
                                                  //| 	How.I.Met.Your.Mother.S06E24.HDTV.XviD-LOL.[VTV].avi
                                                  //| ----Unsere Mütter, Unsere Väter_ Ein anderer Krieg [Teil 2 von 3](360p_H.
                                                  //| 264-AAC).mp4
  println(lines2.drop(10).take(10).mkString("\n"))//> 	Unsere Mütter, Unsere Väter_ Ein anderes Land [Teil 3 von 3](360p_H.26
                                                  //| 4-AAC).mp4
                                                  //| ----Community.S04E11.HDTV.x264-LOL.mp4
                                                  //| 	Community.S04E12.HDTV.x264-LOL.mp4
                                                  //| ----Unsere Mütter, Unsere Väter_ Ein anderes Land [Teil 3 von 3](360p_H.2
                                                  //| 64-AAC).mp4
                                                  //| 	Unsere Mütter, Unsere Väter_ Ein anderer Krieg [Teil 2 von 3](360p_H.2
                                                  //| 64-AAC).mp4
                                                  //| 	unsere.muetter.unsere.vaeter.Teil.3(360p_H.264-AAC).mp4
                                                  //| 	Unsere Mütter, Unsere Väter - Eine andere Zeit [Teil 1 von 3](360p_H.2
                                                  //| 64-AAC).mp4
                                                  //| ----game.of.thrones.s03e01.hdtv.x264-2hd.mp4
                                                  //| 	game.of.thrones.s03e03.proper.hdtv.x264-2hd.mp4
                                                  //| ----Skins.S07E06.HDTV.XviD-AFG.avi
  println(lines2.drop(10).drop(10).take(10).mkString("\n"))
                                                  //> 	Breaking.Bad.S05E16.HDTV.XviD-AFG.avi
                                                  //| ----the.big.bang.theory.621.hdtv-lol.mp4
                                                  //| 	redebeta.com.the.big.bang.theory.624.hdtv-lol.mp4
                                                  //| 	the.big.bang.theory.621.hdtv-lol.sample.mp4
                                                  //| ----Burn.Notice.S06E07.HDTV.x264-COMPULSiON.[VTV].mp4
                                                  //| 	Burn.Notice.S06E10.Desperate.Times.720p.WEB-DL.DD5.1.H264-NTb.mkv
                                                  //| 	Burn.Notice.S06E09.HDTV.x264-ASAP.[VTV].mp4
                                                  //| 	Burn.Notice.S06E06.Shockwave.HDTV.x264-FQM.[VTV].mp4
                                                  //| ----the.big.bang.theory.621.hdtv-lol.sample.mp4
                                                  //| 	the.big.bang.theory.621.hdtv-lol.mp4
  println(lines2.drop(10).drop(10).drop(10).take(10).mkString("\n"))
                                                  //> ----Burn.Notice.S06E06.Shockwave.HDTV.x264-FQM.[VTV].mp4
                                                  //| 	Burn.Notice.S06E07.HDTV.x264-COMPULSiON.[VTV].mp4
                                                  //| ----Game.of.Thrones.S03E06.720p.HDTV.x264-IMMERSE.mkv
                                                  //| 	Game.of.Thrones.S03E02.720p.HDTV.x264-IMMERSE.mkv
                                                  //| ----Game.of.Thrones.S03E02.720p.HDTV.x264-IMMERSE.mkv
                                                  //| 	Game.of.Thrones.S03E06.720p.HDTV.x264-IMMERSE.mkv
                                                  //| ----the.walking.dead.s04e02.720p.hdtv.x264-2hd.mkv
                                                  //| 	sample-the.walking.dead.s04e02.720p.hdtv.x264-2hd.mkv
                                                  //| ----How.I.Met.Your.Mother.S06E24.HDTV.XviD-LOL.[VTV].avi
                                                  //| 	how.i.met.your.mother.821.hdtv-lol.mp4
  println(lines2.drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
                                                  //> ----Game.of.Thrones.S03E04.HDTV.x264-EVOLVE.mp4
                                                  //| 	Game.of.Thrones.S03E10.720p.HDTV.x264-EVOLVE.mkv
                                                  //| 	Game.of.Thrones.S03E09.720p.HDTV.x264-EVOLVE.mkv
                                                  //| ----Game.of.Thrones.S03E09.720p.HDTV.x264-EVOLVE.mkv
                                                  //| 	Game.of.Thrones.S03E04.HDTV.x264-EVOLVE.mp4
                                                  //| ----The.Mentalist.S05E21.HDTV.x264-LOL.mp4
                                                  //| 	The.Mentalist.S05E22.HDTV.x264-LOL.mp4
                                                  //| ----game.of.thrones.s02e04.hdtv.xvid-fqm.avi
                                                  //| 	sample-game.of.thrones.s02e04.hdtv.xvid-fqm.avi
                                                  //| ----game.of.thrones.s03e03.proper.hdtv.x264-2hd.mp4
  println(lines2.drop(10).drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
                                                  //> 	game.of.thrones.s03e01.hdtv.x264-2hd.mp4
                                                  //| ----Community.S04E12.HDTV.x264-LOL.mp4
                                                  //| 	Community.S04E13.HDTV.x264-LOL.mp4
                                                  //| 	Community.S04E11.HDTV.x264-LOL.mp4
                                                  //| 	Borgen.BBC.S01E01.mp4
                                                  //| ----[www.Cpasbien.com] Game.Of.Thrones.S02E05.REPACK.FASTSUB.VOSTFR.HDTV.Xv
                                                  //| iD-F4ST.avi
                                                  //| 	Game.of.Thrones.S03E05.720p.HDTV.x264.Dual.Audio.THESPEEDBR.COM.mkv
                                                  //| 	Game.of.Thrones.S02E02.SWESUB.WEBRip.XviD-[tankafilm.com].avi
                                                  //| ----sample-game.of.thrones.s02e04.hdtv.xvid-fqm.avi
                                                  //| 	game.of.thrones.s02e04.hdtv.xvid-fqm.avi
  println(lines2.drop(10).drop(10).drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
                                                  //> 	Sample.avi
                                                  //| ----Skins.S07E01.HDTV.x264-TLA.mp4
                                                  //| 	skins.s07e05.hdtv.x264-tla.mp4
                                                  //| 	Skins.S07E06.HDTV.XviD-AFG.avi
                                                  //| 	skins.s07e02.720p.hdtv.x264-bia.mkv
                                                  //| 	Skins.S07E04.HDTV.x264-TLA.mp4
                                                  //| ----Skins.S07E04.HDTV.x264-TLA.mp4
                                                  //| 	Skins.S07E01.HDTV.x264-TLA.mp4
                                                  //| ----Game.of.Thrones.S03E08.SWESUB.720p.HDTV.XviD.AC3-Olizzz.avi
                                                  //| 	Game.of.Thrones.S02E03.Swesub.HDTV.XviD-Shareitall.avi
  println(lines2.drop(10).drop(10).drop(10).drop(10).drop(10).drop(10).drop(10).take(10).mkString("\n"))
                                                  //> ----Game.of.Thrones.S02E02.SWESUB.WEBRip.XviD-[tankafilm.com].avi
                                                  //| 	[www.Cpasbien.com] Game.Of.Thrones.S02E05.REPACK.FASTSUB.VOSTFR.HDTV.Xvi
                                                  //| D-F4ST.avi
                                                  //| 	Game.of.Thrones.S03E08.SWESUB.720p.HDTV.XviD.AC3-Olizzz.avi
                                                  //| ----The.Mentalist.S05E22.HDTV.x264-LOL.mp4
                                                  //| 	The.Mentalist.S05E21.HDTV.x264-LOL.mp4
                                                  //| ----sample-the.walking.dead.s04e02.720p.hdtv.x264-2hd.mkv
                                                  //| 	the.walking.dead.s04e02.720p.hdtv.x264-2hd.mkv
                                                  //| ----ZDF Heute Show 2013 Folge 116 vom 05.04.13 in HD(360p_H.264-AAC).mp4
                                                  //| 	heute-show vom 17.05.2013 - komplette Folge vom 17.05. _ 17.5. _ 17.5.20
                                                  //| 13(360p_H.264-AAC).mp4
                                                  //| ----skins.7x03.pure_part_one.720p_hdtv_x264-fov.mkv


}