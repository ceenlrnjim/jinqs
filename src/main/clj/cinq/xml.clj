(ns cinq.xml
  (:import [net.sf.saxon.xqj SaxonXQDataSource])
  (:import [net.sf.saxon Configuration])
  (:import [javax.xml.namespace QName])
  (:import [javax.xml.transform Source])
  (:import [javax.xml.transform.sax SAXSource]))

; TODO: can this be re-used?
(def saxon-src (SaxonXQDataSource. (Configuration.)))

(defn str-src
  [s]
  (SAXSource. (org.xml.sax.InputSource. (java.io.StringReader. s))))

(defn query
  "takes a query and pairs of names & sources and executes the query"
  [q f & nsps]
  ; Note: currently expect the declarations for the sources to be in q - TODO: add automatically?
  (with-open [conn (.getConnection saxon-src)
              expr (.prepareExpression conn q)]
    (doseq [[n s] (partition 2 nsps)]
      (.bindDocument expr (QName. n) ^Source s nil))
    ; TODO: bindings
    (with-open [rs (.executeQuery expr)]
      (loop [result []]
        (let [more? (.next rs)]
          (if (not more?) result
              (recur (conj result (f (.getItem rs))))))))))
        
             
      


