import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MovieAnalyzer {
    String path;

    public static class Movie {
        String Series_Title;
        String Released_Year;
        String Certificate;
        int Runtime;
        String Genre;
        Float IMDB_Rating;
        String Overview;
        String Meta_score;
        String Director, Star1, Star2, Star3, Star4;
        String No_of_Votes, Gross;

        public Movie(String Series_Title, String Released_Year, String Certificate, int Runtime, String Genre,
                     Float IMDB_Rating, String Overview, String Meta_score, String Director, String Star1, String Star2, String Star3,
                     String Star4, String No_of_Votes, String Gross) {
            this.Certificate = Certificate;
            this.Gross = Gross;
            this.Director = Director;
            this.No_of_Votes = No_of_Votes;
            this.Star1 = Star1;
            this.Star2 = Star2;
            this.Star3 = Star3;
            this.Star4 = Star4;
            this.Runtime = Runtime;
            this.Released_Year = Released_Year;
            this.Series_Title = Series_Title;
            this.Overview = Overview;
            this.IMDB_Rating = IMDB_Rating;
            this.Meta_score = Meta_score;
            this.Genre = Genre;
        }
        public String getReleased_Year(){
            return Released_Year;
        }
        public String getGenre(){
            return Genre;
        }
        public String getStar1(){
            return Star1;
        }
        public String getStar2(){
            return Star2;
        }
        public String getStar3(){
            return Star3;
        }
        public String getStar4(){
            return Star4;
        }
        public int getRuntime(){
            return Runtime;
        }
        public String getOverview(){
            return Overview;
        }
    }

    public static Stream<Movie> readMovies(String filename) throws IOException {
        return Files.lines(Paths.get(filename)).skip(1)
                .map(l ->{String[] a= l.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", -1);
                    for (int i=0;i<a.length;i++){
                        if(a[i].length()!=0){
                        if (a[i].charAt(0)=='"'){
                            a[i]=a[i].substring(1,a[i].length()-1);}
                        }
                    }
                    return a;

                })
                .map(a -> new Movie(a[1], a[2], a[3], Integer.parseInt(a[4].replace(" min","")), a[5], Float.parseFloat(a[6]),
                     a[7],a[8], a[9], a[10], a[11], a[12], a[13], a[14], a[15]));
    }

    public MovieAnalyzer(String dataset_path)  {
        this.path=dataset_path;
    }

    public Map<Integer, Integer> getMovieCountByYear() throws IOException {
        Stream<Movie> s=readMovies(this.path);
        Map<String, List<Movie>> l= s.collect(Collectors.groupingBy(Movie::getReleased_Year));
        Map<Integer,Integer> j = new TreeMap<>(Comparator.reverseOrder());
        l.forEach((k,v)->j.put(Integer.parseInt(k), v.size()));
        return j;
    }

    public Map<String, Integer> getMovieCountByGenre() throws IOException {
        Stream<Movie> s=readMovies(this.path);
        Map<String, List<Movie>> l= s.collect(Collectors.groupingBy(Movie::getGenre));
        Map<String,Integer> j = new TreeMap<>();
        l.forEach((k,v)->{String[] u=k.replace("\"","").split(", ");
        for (String e:u){
            if (j.containsKey(e)){
                j.replace(e,j.get(e)+v.size());
            }
            else {
                j.put(e,v.size());
            }
        }
        });
        Map<String,Integer> i = new TreeMap<>((o1,o2)->{
            if (j.get(o1)>j.get(o2)){
                return -1;
            }
            else if (j.get(o1)<j.get(o2)){
                return 1;
            }
            else {
                return o1.compareTo(o2);
            }
        });
        i.putAll(j);
        return i;
    }
    public Map<List<String>, Integer> getCoStarCount() throws IOException {
        Stream<Movie> s=readMovies(this.path);
        Map<String, List<Movie>> l1= s.collect(Collectors.groupingBy(Movie::getStar1));
         s=readMovies(this.path);
        Map<String, List<Movie>> l2= s.collect(Collectors.groupingBy(Movie::getStar2));
         s=readMovies(this.path);
        Map<String, List<Movie>> l3= s.collect(Collectors.groupingBy(Movie::getStar3));
         s=readMovies(this.path);
        Map<String, List<Movie>> l4= s.collect(Collectors.groupingBy(Movie::getStar4));
        Map<String, List<Movie>> l=new TreeMap<>();
        l1.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);
            }
            else {
                l.put(k,v);
            }
        });
        l2.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);
            }
            else {
                l.put(k,v);
            }
        });
        l3.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);
            }
            else {
                l.put(k,v);
            }
        });
        l4.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);

            }
            else {
                l.put(k,v);
            }
        });
        l.remove("");
        Map<String,Integer> i=new HashMap<>();
        l.forEach((k,v)->{

            for (Movie t:v){
                int ij=0;
                for (Movie t1:v){
                    if (t1.Series_Title.equals(t.Series_Title)){
                        ij++;
                    }
                }
                if (ij==2){
                    String q=k+", "+k;
                    if (i.containsKey(q)){
                        i.replace(q,i.get(q)+1);
                    }
                    else {
                        i.put(q,1);
                    }
                }
                String c;
                if (!(c=t.Star1).equals(k)){
                    String q;
                    if (c.compareTo(k) < 0){
                        q=c+", "+k;
                    }
                    else {q=k+", "+c;}
                    if (i.containsKey(q)){
                        i.replace(q,i.get(q)+1);
                    }
                    else {
                        i.put(q,1);
                    }
                }
                if (!(c=t.Star2).equals(k)){
                    String q;
                    if (c.compareTo(k) < 0){
                        q=c+", "+k;
                    }
                    else {q=k+", "+c;}
                    if (i.containsKey(q)){
                        i.replace(q,i.get(q)+1);
                    }
                    else {
                        i.put(q,1);
                    }
                }
                if (!(c=t.Star3).equals(k)){
                    String q;
                    if (c.compareTo(k) < 0){
                        q=c+", "+k;
                    }
                    else {q=k+", "+c;}
                    if (i.containsKey(q)){
                        i.replace(q,i.get(q)+1);
                    }
                    else {
                        i.put(q,1);
                    }
                }
                if (!(c=t.Star4).equals(k)){
                    String q;
                    if (c.compareTo(k) < 0){
                        q=c+", "+k;
                    }
                    else {q=k+", "+c;}
                    if (i.containsKey(q)){
                        i.replace(q,i.get(q)+1);
                    }
                    else {
                        i.put(q,1);
                    }
                }

            }
        });

        Map<List<String>,Integer> ww=new HashMap<>();
        i.forEach((k,v)->{
            List<String> yy= List.of(k.split(", "));
            ww.put(yy,v/2);
        });
        return ww;
    }
    public List<String> getTopMovies(int top_k, String by) throws IOException {
        if (by.equals("runtime")){
        Stream<Movie> s=readMovies(this.path);
         List<Movie> l= s.sorted((o1,o2)->{
             if (o1.getRuntime()>o2.getRuntime()){
                 return -1;
             }
             else if (o1.getRuntime()<o2.getRuntime()){
                 return 1;
             }
             else {
                 return o1.Series_Title.compareTo(o2.Series_Title);
             }
                 })
                 .limit(top_k).toList();
         List<String> i=new ArrayList<>();
         l.forEach(x->i.add(x.Series_Title));
         return i;
        }

        else {
            Stream<Movie> s=readMovies(this.path);
            List<Movie> l= s.sorted((o1,o2)->{
                        if (o1.getOverview().length()>o2.getOverview().length()){
                            return -1;
                        }
                        else if (o1.getOverview().length()<o2.getOverview().length()){
                            return 1;
                        }
                        else {
                            return o1.Series_Title.compareTo(o2.Series_Title);
                        }
                    })
                    .limit(top_k).toList();
            List<String> i=new ArrayList<>();
            l.forEach(x->i.add(x.Series_Title));
            return i;
        }
    }
    public List<String> getTopStars(int top_k, String by) throws IOException {
        Stream<Movie> s=readMovies(this.path);
        Map<String, List<Movie>> l1= s.collect(Collectors.groupingBy(Movie::getStar1));
        s=readMovies(this.path);
        Map<String, List<Movie>> l2= s.collect(Collectors.groupingBy(Movie::getStar2));
        s=readMovies(this.path);
        Map<String, List<Movie>> l3= s.collect(Collectors.groupingBy(Movie::getStar3));
        s=readMovies(this.path);
        Map<String, List<Movie>> l4= s.collect(Collectors.groupingBy(Movie::getStar4));
        Map<String, List<Movie>> l=new TreeMap<>();
        l1.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);
            }
            else {
                l.put(k,v);
            }
        });
        l2.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);
            }
            else {
                l.put(k,v);
            }
        });
        l3.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);
            }
            else {
                l.put(k,v);
            }
        });
        l4.forEach((k,v)-> {
            if (l.containsKey(k)){
                l.get(k).addAll(v);

            }
            else {
                l.put(k,v);
            }
        });
        l.remove("");
        if (by.equals("rating")){
            Map<String,Double > i=new TreeMap<>();
            l.forEach((k,v)->{
                double e=0.0000000000d;
                for (Movie t:v){
                    e+=t.IMDB_Rating;
                }
                e=e/ v.size();
                i.put(k,e);
            });
            Map<String,Double > w=new TreeMap<>((o1,o2)->{
                if (i.get(o1)>i.get(o2)){
                    return -1;
                }
                else if (i.get(o1)<i.get(o2)){
                    return 1;
                }
                else {
                    return o1.compareTo(o2);
                }
            });
            w.putAll(i);
            List<String> a=new ArrayList<>(w.keySet());
            List<String> b=new ArrayList<>();
            for (int j=0;j<top_k;j++){
                b.add(a.get(j));
            }
            return b;
        }
        else {
            Map<String,Long > i=new TreeMap<>();
            l.forEach((k,v)->{
                long e=0;
                long r=v.size();
                for (Movie t:v){
                    if (!t.Gross.equals("")){
                   e+= Long.parseLong(t.Gross.replace(",",""));}
                    else {
                        r-=1;
                    }
                }
                if (r>0){
                e=e/ r;}
                else e=0;
                i.put(k,e);
            });
            Map<String,Long > w=new TreeMap<>((o1,o2)->{
                if (i.get(o1)>i.get(o2)){
                    return -1;
                }
                else if (i.get(o1)<i.get(o2)){
                    return 1;
                }
                else {
                    return o1.compareTo(o2);
                }
            });
            w.putAll(i);
            List<String> a=new ArrayList<>(w.keySet());
            List<String> b=new ArrayList<>();
            for (int j=0;j<top_k;j++){
                b.add(a.get(j));
            }
            return b;
        }
    }
    public List<String> searchMovies(String genre, float min_rating, int max_runtime) throws IOException {
        Stream<Movie> s=readMovies(this.path);
        return s.filter(x->{
            boolean y1=false;
            String[] z=x.Genre.split(", ");
            for (String a:z){
                if (a.equals(genre)) {
                    y1 = true;
                    break;
                }
            }
            return y1&(x.IMDB_Rating>=min_rating)&(x.getRuntime()<=max_runtime);
        }).map(x->x.Series_Title).sorted((String::compareTo)).toList();

        
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        MovieAnalyzer movieAnalyzer=new MovieAnalyzer("D:\\A1_Sample\\A1_Sample\\resources\\imdb_top_500.csv");
        System.out.println(readMovies(movieAnalyzer.path).toList().get(5).Series_Title);
        System.out.println(movieAnalyzer.getTopStars(15,"rating"));
        List<String> a=movieAnalyzer.getTopStars(100,"gross");
        BufferedWriter out=new BufferedWriter(new FileWriter("D:\\A1_Sample\\A1_Sample\\resources\\answers_local\\q31.txt"));
        for (String t:a){
            out.write(t);
            out.newLine();
        }
        out.close();
    }
}
