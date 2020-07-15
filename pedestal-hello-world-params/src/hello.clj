(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [clojure.string :as str]))

(defn ok [body]
  { :status 200 :body body })

;; (defn greeting-for [firstName lastName]
;;   (if (and (empty? firstName) (empty? lastName))
;;     "Hello, world! \n"
;;     (str "Hello, " firstName " " lastName "!")))
;;     

;; (defn greeting-for [firstName lastName]
;;   (cond
;;     (and (empty? firstName) (empty? lastName)) "Hello, world!"
;;     (and (empty? firstName) (not (empty? lastName))) (str "Hello, Mr " lastName "!")
;;     (and (not (empty? firstName)) (empty? lastName)) (str "Hello, " firstName "!")
;;     ))

(defn greet [firstName lastName]
  (->>
   (list "Hello," firstName lastName "!")
   (map #(str % " "))
   (reduce str)
   (str/trim)))

(defn yalla
  ([] "Hello, world!")
  ([firstName] (str "Hello, " firstName "!"))
  ([firstName lastName] (greet firstName lastName)))

(defn respond-hello [request]
  { :status 200 :body request })

(defn respond-hello-param [request]
  (let [firstName (get-in request [:query-params :firstName])
        lastName (get-in request [:query-params :lastName])
        params (filter #(not (nil? %)) (list firstName lastName))
        resp (eval `(yalla ~@params))]
    (ok resp)))

(def routes
  (route/expand-routes
   #{["/greet" :get respond-hello-param :route-name :greet]}))

(defn create-server []
  (http/create-server
   {::http/routes routes
    ::http/type   :jetty
    ::http/port   4567}))


(def service-map
  {::http/routes routes
   ::http/type   :jetty
   ::http/port   4567})

(defn start []
  (http/start (http/create-server service-map)))

(defonce server (atom nil))

(defn start-dev []
  (reset! server
          (http/start (http/create-server
                       (assoc service-map
                              ::http/join? false)))))

(defn stop-dev []
  (http/stop @server))

(defn restart []
  (stop-dev)
  (start-dev))
