import json
from pathlib import Path

import mysql.connector
import pandas as pd
import matplotlib.pyplot as plt


# Datos para conectar con la base de datos
CONFIG_BD = {
    "host": "localhost",
    "port": 3306,
    "user": "root",
    "password": "",
    "database": "circularmarket"
}


def obtener_ruta_proyecto():
    # Busca la carpeta principal del proyecto
    return Path(__file__).resolve().parent.parent


def obtener_registros_usuarios():
    # Se conecta a MySQL
    conexion = mysql.connector.connect(**CONFIG_BD)

    consulta = """
        SELECT
            DATE(UScreadoen) AS fecha,
            COUNT(*) AS total_usuarios
        FROM usuarios
        WHERE UScreadoen IS NOT NULL
        GROUP BY DATE(UScreadoen)
        ORDER BY DATE(UScreadoen) ASC;
    """

    # Guarda el resultado de la consulta en una tabla de pandas
    df = pd.read_sql(consulta, conexion)
    conexion.close()

    return df


def completar_dias_sin_registros(df):
    if df.empty:
        return df

    df["fecha"] = pd.to_datetime(df["fecha"])

    fecha_inicio = df["fecha"].min()
    fecha_fin = df["fecha"].max()

    # Crea todas las fechas entre el primer y el último registro
    rango_fechas = pd.date_range(start=fecha_inicio, end=fecha_fin, freq="D")

    df_completo = pd.DataFrame({"fecha": rango_fechas})

    # Junta las fechas completas con los datos de usuarios
    df_completo = df_completo.merge(
        df,
        on="fecha",
        how="left"
    )

    # Si un día no tiene registros, pone 0
    df_completo["total_usuarios"] = df_completo["total_usuarios"].fillna(0).astype(int)

    # Suma los usuarios día a día para sacar el total acumulado
    df_completo["total_acumulado"] = df_completo["total_usuarios"].cumsum()

    return df_completo


def calcular_resumen(df):
    hoy = pd.Timestamp.today().normalize()
    inicio_semana_actual = hoy - pd.Timedelta(days=hoy.weekday())
    inicio_semana_anterior = inicio_semana_actual - pd.Timedelta(days=7)

    # Usuarios registrados esta semana
    usuarios_semana_actual = int(
        df[
            (df["fecha"] >= inicio_semana_actual) &
            (df["fecha"] <= hoy)
        ]["total_usuarios"].sum()
    )

    # Usuarios registrados la semana pasada
    usuarios_semana_anterior = int(
        df[
            (df["fecha"] >= inicio_semana_anterior) &
            (df["fecha"] < inicio_semana_actual)
        ]["total_usuarios"].sum()
    )

    # Diferencia entre esta semana y la anterior
    variacion = usuarios_semana_actual - usuarios_semana_anterior

    # Calcula el porcentaje evitando dividir entre 0
    if usuarios_semana_anterior == 0:
        porcentaje_variacion = 100.0 if usuarios_semana_actual > 0 else 0.0
    else:
        porcentaje_variacion = (variacion / usuarios_semana_anterior) * 100

    total_usuarios = int(df["total_usuarios"].sum())
    total_acumulado = int(df["total_acumulado"].max())

    # Busca el día en el que se registraron más usuarios
    dia_mas_registros = df.loc[df["total_usuarios"].idxmax()]

    resumen = {
        "totalUsuariosAnalizados": total_usuarios,
        "totalAcumulado": total_acumulado,
        "usuariosSemanaActual": usuarios_semana_actual,
        "usuariosSemanaAnterior": usuarios_semana_anterior,
        "variacionUsuariosSemana": variacion,
        "porcentajeVariacionUsuariosSemana": round(porcentaje_variacion, 2),
        "diaMasRegistrosFecha": dia_mas_registros["fecha"].strftime("%d/%m/%Y"),
        "diaMasRegistrosTotal": int(dia_mas_registros["total_usuarios"]),
        "primerRegistro": df["fecha"].min().strftime("%d/%m/%Y"),
        "ultimoRegistro": df["fecha"].max().strftime("%d/%m/%Y")
    }

    return resumen


def generar_estadistica(df):
    if df.empty:
        print("No hay datos suficientes para generar la estadística.")
        return

    # Prepara los datos y calcula el resumen
    df = completar_dias_sin_registros(df)
    resumen = calcular_resumen(df)

    ruta_proyecto = obtener_ruta_proyecto()

    # Carpeta donde se guardan los archivos generados
    carpeta_static = ruta_proyecto / "src" / "main" / "resources" / "static" / "img" / "estadisticas"
    carpeta_static.mkdir(parents=True, exist_ok=True)

    ruta_png = carpeta_static / "usuarios_registrados_por_dia.png"
    ruta_csv = carpeta_static / "usuarios_registrados_por_dia.csv"
    ruta_json = carpeta_static / "estadisticas_usuarios.json"

    # Guarda la tabla de datos en un archivo CSV
    df.to_csv(ruta_csv, index=False, encoding="utf-8-sig")

    # Guarda el resumen en un archivo JSON
    with open(ruta_json, "w", encoding="utf-8") as archivo_json:
        json.dump(resumen, archivo_json, ensure_ascii=False, indent=4)

    fig, ax = plt.subplots(figsize=(11, 4.2))

    # Dibuja la línea de usuarios por día
    ax.plot(
        df["fecha"],
        df["total_usuarios"],
        marker="o",
        linewidth=2.4,
        label="Usuarios registrados por día"
    )

    # Dibuja la línea del total acumulado
    ax.plot(
        df["fecha"],
        df["total_acumulado"],
        marker="o",
        linewidth=2.4,
        label="Total acumulado"
    )

    ax.set_title("Evolución de registros de usuarios - CircularMarket", fontsize=14, fontweight="bold", pad=12)
    ax.set_xlabel("Fecha", fontsize=10)
    ax.set_ylabel("Número de usuarios", fontsize=10)

    ax.grid(True, linestyle="--", alpha=0.35)
    ax.legend(loc="upper left", frameon=True)

    ax.tick_params(axis="x", rotation=35)
    ax.tick_params(axis="both", labelsize=9)

    fig.tight_layout()

    # Guarda la gráfica como imagen
    fig.savefig(
        ruta_png,
        dpi=180,
        bbox_inches="tight",
        facecolor="white"
    )

    plt.close(fig)

    print("Estadística generada correctamente.")
    print(f"Imagen generada en: {ruta_png}")
    print(f"CSV generado en: {ruta_csv}")
    print(f"JSON generado en: {ruta_json}")
    print()
    print("Resumen:")
    print(f"- Total de usuarios analizados: {resumen['totalUsuariosAnalizados']}")
    print(f"- Esta semana: {resumen['usuariosSemanaActual']}")
    print(f"- Semana anterior: {resumen['usuariosSemanaAnterior']}")
    print(f"- Variación: {resumen['variacionUsuariosSemana']} usuarios")
    print(f"- Porcentaje de variación: {resumen['porcentajeVariacionUsuariosSemana']}%")
    print(f"- Día con más registros: {resumen['diaMasRegistrosFecha']} ({resumen['diaMasRegistrosTotal']} usuarios)")


def main():
    try:
        df = obtener_registros_usuarios()
        generar_estadistica(df)

    except mysql.connector.Error as error:
        print("Error al conectar con MySQL.")
        print(f"Detalle: {error}")

    except Exception as error:
        print("Error inesperado al generar la estadística.")
        print(f"Detalle: {error}")


# Inicia el programa
if __name__ == "__main__":
    main()