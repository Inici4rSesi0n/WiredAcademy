/*
 * SPDX-FileCopyrightText: 2026 inici4rsesi0n
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package integration.eventos;
import dominio.modelo.TipoEvento;
import dominio.puerto.externo.EventBusListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
/**
 *
 * @author inici4rsesi0n
 */
public class TestEventRecorder implements EventBusListener {
    private final List<TipoEvento> recibidos = new CopyOnWriteArrayList<>();
    @Override
    public void onEvent(TipoEvento evento) {
        recibidos.add(evento);
    }
    public void reset() {
        recibidos.clear();
    }
    public List<TipoEvento> recibidos() {
        return List.copyOf(recibidos);
    }
    public boolean contiene(TipoEvento evento) {
        return recibidos.contains(evento);
    }
}
