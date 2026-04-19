import json
import os
from urllib import error as urllib_error
from urllib import parse as urllib_parse
from urllib import request as urllib_request

from flask import Blueprint, jsonify, render_template, request

COMANDO_ACTUALIZAR_PUNTUACION = "ACTUALIZAR_PUNTUACION"


def create_space_invaders_blueprint(base_dir: str) -> Blueprint:
    if not isinstance(base_dir, str) or not base_dir.strip():
        raise ValueError("base_dir es obligatorio para crear el blueprint de Space Invaders")

    backend_base_url = os.getenv(
        "SPACE_INVADERS_BACKEND_URL",
        "http://127.0.0.1:8082/api/pruebas/space-invaders",
    ).rstrip("/")

    blueprint = Blueprint(
        "space_invaders",
        __name__,
        template_folder=os.path.join(base_dir, "src", "juegos", "SpaceInvaders", "templates"),
        static_folder=os.path.join(base_dir, "src", "juegos", "SpaceInvaders", "static"),
        static_url_path="/juegos/SpaceInvaders/static",
    )

    @blueprint.route("/")
    def space_invaders_home():
        return render_template("space_invaders.html")

    @blueprint.route("/pantalla")
    def space_invaders_pantalla():
        screen_id = request.args.get("screenId", type=str)
        if not isinstance(screen_id, str) or not screen_id.strip():
            screen_id = "pantalla-principal"

        return render_template("space_invaders_scoreboard.html", screen_id=screen_id.strip())

    def _extraer_player_id_desde_query():
        player_id = request.args.get("playerId", type=str)
        if not isinstance(player_id, str) or not player_id.strip():
            return None

        return player_id.strip()

    def _extraer_screen_id_desde_query():
        screen_id = request.args.get("screenId", type=str)
        if not isinstance(screen_id, str) or not screen_id.strip():
            return None

        return screen_id.strip()

    def _invocar_backend(method, path, payload=None, query=None):
        url = f"{backend_base_url}{path}"
        if query:
            url = f"{url}?{urllib_parse.urlencode(query)}"

        headers = {
            "Accept": "application/json",
        }
        data_bytes = None

        if payload is not None:
            headers["Content-Type"] = "application/json"
            data_bytes = json.dumps(payload).encode("utf-8")

        req = urllib_request.Request(url=url, data=data_bytes, headers=headers, method=method)

        try:
            with urllib_request.urlopen(req, timeout=4) as response:
                return response.getcode(), response.read(), response.headers.get("Content-Type", "application/json")
        except urllib_error.HTTPError as error:
            return error.code, error.read(), error.headers.get("Content-Type", "application/json")
        except Exception:
            return None, None, None

    def _respuesta_proxy(status_code, body_bytes, _content_type):
        if status_code is None:
            return jsonify({
                "status": "error",
                "message": "No se pudo conectar con el backend Java de Space Invaders",
                "backendBaseUrl": backend_base_url,
            }), 502

        if status_code == 204:
            return ("", 204)

        if not body_bytes:
            return ("", status_code)

        try:
            body_json = json.loads(body_bytes.decode("utf-8"))
            return jsonify(body_json), status_code
        except Exception:
            return body_bytes, status_code

    @blueprint.route("/api/score", methods=["GET"])
    def space_invaders_get_score():
        status_code, body_bytes, content_type = _invocar_backend("GET", "/score")
        return _respuesta_proxy(status_code, body_bytes, content_type)

    @blueprint.route("/api/updates", methods=["GET"])
    def space_invaders_get_updates():
        player_id = _extraer_player_id_desde_query()
        screen_id = _extraer_screen_id_desde_query()
        if player_id is None and screen_id is None:
            return jsonify({"status": "error", "message": "Missing query param 'playerId' or 'screenId'"}), 400

        query = {}
        if screen_id is not None:
            query["screenId"] = screen_id
        else:
            query["playerId"] = player_id

        status_code, body_bytes, content_type = _invocar_backend(
            "GET",
            "/updates",
            query=query,
        )
        return _respuesta_proxy(status_code, body_bytes, content_type)

    @blueprint.route("/api/event", methods=["POST"])
    def space_invaders_procesar_evento():
        data = request.get_json()
        if not isinstance(data, dict):
            return jsonify({"status": "error", "message": "Invalid JSON"}), 400

        status_code, body_bytes, content_type = _invocar_backend("POST", "/event", payload=data)
        return _respuesta_proxy(status_code, body_bytes, content_type)

    @blueprint.route("/api/score", methods=["POST"])
    def space_invaders_save_score():
        data = request.get_json()
        if data and "score" in data and "player" in data:
            player_name = str(data["player"]).strip()

            if player_name:
                try:
                    score = int(data["score"])
                except (TypeError, ValueError):
                    return jsonify({"status": "error", "message": "Score must be numeric"}), 400

                event_payload = {
                    "comando": COMANDO_ACTUALIZAR_PUNTUACION,
                    "jugadorId": f"name:{player_name.lower()}",
                    "nombreJugador": player_name,
                    "puntuacion": score,
                }

                status_code, body_bytes, content_type = _invocar_backend(
                    "POST",
                    "/event",
                    payload=event_payload,
                )
                return _respuesta_proxy(status_code, body_bytes, content_type)

        return jsonify({"status": "error", "message": "Invalid data, Requires 'player' and 'score'"}), 400

    return blueprint
