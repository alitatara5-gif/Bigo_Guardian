from flask import Flask, render_template, request, session, redirect, url_for
import os
import json
import time

# Flask diatur supaya baca folder web/templates dan web/static
app = Flask(__name__, template_folder='web/templates', static_folder='web/static')
app.secret_key = 'posix_ultimate_sync_v2'

def get_bigo_thumb(user_id):
    try:
        ua = "Mozilla/5.0 (Linux; Android 10; Infinix Hot 40i)"
        cmd = f"curl -s -L -A '{ua}' 'https://ta.bigo.tv/official_website/studio/getInternalStudioInfo?siteId={user_id}'"
        output = os.popen(cmd).read()
        data = json.loads(output)
        thumb = data.get("data", {}).get("snapshot", "")
        if thumb and "http" in thumb:
            return thumb
    except:
        pass
    return url_for('static', filename='icon.png')

@app.route('/')
def index():
    if 'workers' not in session: session['workers'] = []
    return render_template('index.html', workers=session['workers'])

@app.route('/handle', methods=['POST'])
def handle():
    if 'workers' not in session: session['workers'] = []
    bid = request.form.get('bigo_id')
    action = request.form.get('action')

    if action == "add":
        bid = bid.strip()
        if not any(w['id'] == bid for w in session['workers']) and len(session['workers']) < 20:
            # Kirim perintah ke Tmux tempat Golang berjalan
            os.system(f"tmux send-keys -t bigo.1 '{bid}' C-m")
            thumb_url = get_bigo_thumb(bid)
            new_worker = {"id": bid, "thumb": thumb_url, "start_time": int(time.time())}
            temp = session['workers']
            temp.insert(0, new_worker)
            session['workers'] = temp

    elif action == "stop":
        os.system(f"tmux send-keys -t bigo.1 'stop {bid}' C-m")
        temp = [w for w in session['workers'] if w['id'] != bid]
        session['workers'] = temp

    return redirect(url_for('index'))

if __name__ == '__main__':
    print("[*] Menjalankan Dashboard Bigo POSIX di http://127.0.0.1:8080")
    app.run(host='0.0.0.0', port=8080)
