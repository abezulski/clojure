(ns hello
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]))

(defn ok [body]
  { :status 200 :body body })

(defn greeting-for [firstName lastName]
  (or (empty? firstName) (empty? lastName)
    "Hello, world! \n"
    (str "Hello, " firstName lastName "\n")))

(defn respond-hello [request]
  { :status 200 :body request })

(defn respond-hello-param [request]
  (let [firstName (get-in request [:query-params :firstName])
        lastName (get-in request [:query-params :lastName])
        resp (greeting-for firstName lastName)]
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